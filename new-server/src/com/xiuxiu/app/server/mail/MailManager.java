package com.xiuxiu.app.server.mail;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mail.PCLIMailNtfAddInfo;
import com.xiuxiu.app.server.BaseManager;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.constant.EMoneyType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.db.UIDManager;
import com.xiuxiu.app.server.db.UIDType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.table.TbMailManager;
import com.xiuxiu.core.net.Task;
import com.xiuxiu.xpush.XPushManager;

public class MailManager extends BaseManager {
    private static class MailManagerHolder {
        private static MailManager instance = new MailManager();
    }

    public static MailManager I = MailManagerHolder.instance;

    private MailManager() {
    }

    public boolean sendSystemMail(long receivePlayerUid, String title, String content, HashMap<Integer, Integer> item) {
        Player player = PlayerManager.I.getPlayer(receivePlayerUid);
        if (null == player) {
            Logs.MAIL.warn("%s 接收者不存在, 发送邮件失败", receivePlayerUid);
            return false;
        }
        Mail mail = new Mail();
        mail.setUid(UIDManager.I.getAndInc(UIDType.MAIL));
        mail.setSenderPlayerUid(-1);
        mail.setReceivePlayerUid(receivePlayerUid);
        mail.setTitle(title);
        mail.setContent(content);
        mail.setState(0);
        mail.setSendTime(System.currentTimeMillis());
        if (null != item) {
            for (Map.Entry<Integer, Integer> entry : item.entrySet()) {
                int itemId = entry.getKey();
                int num = entry.getValue();
                if (num > 0 && itemId > EMoneyType.NORMAL.getValue() && itemId <= EMoneyType.TICKET.getValue()) {
                    mail.getItem().put(itemId, num);
                    mail.setItemState(1);
                }
            }
        }
        // TODO save
        DBManager.I.save(new Task() {
            @Override
            public void run() {
                DBManager.I.getMailDao().save(mail);
            }
        });
        if (player.isOnline()) {
            PCLIMailNtfAddInfo addInfo = new PCLIMailNtfAddInfo();
            addInfo.mailInfo = mail.toProtocol();
            player.send(CommandId.CLI_NTF_MAIL_ADD, addInfo);
        }
        return true;
    }

    public boolean sendSystemMailWithServer(String title, String content, HashMap<Integer, Integer> item) {
        final Mail mail = new Mail();
        mail.setSenderPlayerUid(-1);
        mail.setTitle(title);
        mail.setContent(content);
        mail.setState(0);
        mail.setSendTime(System.currentTimeMillis());
        if (null != item) {
            for (Map.Entry<Integer, Integer> entry : item.entrySet()) {
                int itemId = entry.getKey();
                int num = entry.getValue();
                if (num > 0 && itemId > EMoneyType.NORMAL.getValue() && itemId <= EMoneyType.TICKET.getValue()) {
                    mail.getItem().put(itemId, num);
                    mail.setItemState(1);
                }
            }
        }

        DBManager.I.save(new Task() {
            @Override
            public void run() {
                List<Long> allPlayerUid = DBManager.I.getPlayerDao().loadAllUid();
                int step = 0;
                Logs.MAIL.debug("begin sendSystemMailWithServer setp:%d", step);
                Iterator<Long> it = allPlayerUid.iterator();
                while (it.hasNext()) {
                    long playerUid = it.next();
                    Player player = PlayerManager.I.getPlayer(playerUid);
                    if (null == player) {
                        Logs.MAIL.warn("%s 接收者不存在, 发送邮件失败", playerUid);
                        continue;
                    }
                    mail.setUid(UIDManager.I.getAndInc(UIDType.MAIL));
                    mail.setReceivePlayerUid(playerUid);
                    // TODO save
                    DBManager.I.getMailDao().save(mail);
                    if (player.isOnline()) {
                        PCLIMailNtfAddInfo addInfo = new PCLIMailNtfAddInfo();
                        addInfo.mailInfo = mail.toProtocol();
                        player.send(CommandId.CLI_NTF_MAIL_ADD, addInfo);
                    }
                    Logs.MAIL.debug("sendSystemMailWithServer setp:%d", step);
                    if (++step >= 1000) {
                        Thread.yield();
                        step = 0;
                    }
                }
                Logs.MAIL.debug("end sendSystemMailWithServer setp:%d", step);
            }
        });
        return true;
    }

    public List<Mail> getAllMailByPlayerUid(long playerUid) {
        List<Mail> list = DBManager.I.getMailDao().loadByPlayerUid(playerUid);
        if (null == list) {
            return Collections.EMPTY_LIST;
        }
        return list;
    }

    public ErrorCode readMail(Player player, long mailUid) {
        Mail mail = player.getMail(mailUid);
        if (null == mail) {
            Logs.MAIL.warn("%s 邮件:%d 不存在", player, mailUid);
            return ErrorCode.MAIL_NOT_EXISTS;
        }
        if (0 != mail.getState()) {
            Logs.MAIL.warn("%s 邮件:%s 已经的读取过了", player, mail);
            return ErrorCode.MAIL_ALREADY_READ;
        }
        mail.setState(1);
        this.updateMail(mail);
        return ErrorCode.OK;
    }

    public ErrorCode delMail(Player player, long mailUid) {
        Mail mail = player.getMail(mailUid);
        if (null == mail) {
            Logs.MAIL.warn("%s 邮件:%d 不存在", player, mailUid);
            return ErrorCode.MAIL_NOT_EXISTS;
        }
        if (1 == mail.getItemState()) {
            Logs.MAIL.warn("%s 邮件:%s 奖励还没领取无法删除", player, mail);
            return ErrorCode.REQUEST_INVALID;
        }
        player.delMail(mailUid);
        mail.setState(2);
        this.updateMail(mail);
        return ErrorCode.OK;
    }

    public List<Long> quickDelMail(Player player) {
        List<Mail> delList = player.delAllMailByReadAndReceive();
        if (delList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<Long> delMailUids = new LinkedList<>();
        Iterator<Mail> it = delList.iterator();
        while (it.hasNext()) {
            Mail mail = it.next();
            delMailUids.add(mail.getUid());
        }
        this.updateMail(delList);
        return delMailUids;
    }

    public ErrorCode receiveMailItem(Player player, long mailUid) {
        Mail mail = player.getMail(mailUid);
        if (null == mail) {
            Logs.MAIL.warn("%s 邮件:%d 不存在", player, mailUid);
            return ErrorCode.MAIL_NOT_EXISTS;
        }
        if (0 == mail.getItemState()) {
            Logs.MAIL.warn("%s 邮件:%s 附件不可领取", player, mail);
            return ErrorCode.MAIL_ITEM_NOT_RECEIVE;
        } else if (2 == mail.getItemState()) {
            Logs.MAIL.warn("%s 邮件:%s 附件已经领取", player, mail);
            return ErrorCode.MAIL_ITEM_ALREADY_RECEIVE;
        }
        mail.setState(1);
        mail.setItemState(2);
        player.receiveMailItem(mail);
        this.updateMail(mail);
        return ErrorCode.OK;
    }

    public List<Mail> quickReceiveMailItem(Player player) {
        List<Mail> receiveList = player.receiveAllMailItem();
        if (receiveList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        this.updateMail(receiveList);
        return receiveList;
    }

    private void updateMail(Mail mail) {
        DBManager.I.save(new Task() {
            @Override
            public void run() {
                DBManager.I.getMailDao().save(mail);
            }
        });
    }

    private void updateMail(List<Mail> mail) {
        DBManager.I.save(new Task() {
            @Override
            public void run() {
                Iterator<Mail> it = mail.iterator();
                while (it.hasNext()) {
                    DBManager.I.getMailDao().save(it.next());
                }
            }
        });
    }

    public boolean sendMail() {
        return false;
    }

    public void sendFirstMail(Player receivePlayer) {
        List<Mail> list = TbMailManager.I.getFirstMail();
        for (Mail mail : list) {
            mail.setUid(UIDManager.I.getAndInc(UIDType.MAIL));
            mail.setReceivePlayerUid(receivePlayer.getUid());
            // TODO save
            DBManager.I.save(new Task() {
                @Override
                public void run() {
                    DBManager.I.getMailDao().save(mail);
                }
            });
        }
    }

    public boolean sendSystemMailWithServer(String title, String content, int itemId, int num) {
        final Mail mail = new Mail();
        mail.setSenderPlayerUid(-1);
        mail.setTitle(title);
        mail.setContent(content);
        mail.setState(0);
        mail.setSendTime(System.currentTimeMillis());
        if (num > 0 && itemId > EMoneyType.NORMAL.getValue() && itemId <= EMoneyType.TICKET.getValue()) {
            mail.getItem().put(itemId, num);
            mail.setItemState(1);
        }

        DBManager.I.save(new Task() {
            @Override
            public void run() {
                List<Long> allPlayerUid = DBManager.I.getPlayerDao().loadAllUid();
                int step = 0;
                Logs.MAIL.debug("begin sendSystemMailWithServer setp:%d", step);
                Iterator<Long> it = allPlayerUid.iterator();
                while (it.hasNext()) {
                    long playerUid = it.next();
                    Player player = PlayerManager.I.getPlayer(playerUid);
                    if (null == player) {
                        Logs.MAIL.warn("%s 接收者不存在, 发送邮件失败", playerUid);
                        continue;
                    }
                    mail.setUid(UIDManager.I.getAndInc(UIDType.MAIL));
                    mail.setReceivePlayerUid(playerUid);
                    // TODO save
                    DBManager.I.getMailDao().save(mail);
                    if (player.isOnline()) {
                        PCLIMailNtfAddInfo addInfo = new PCLIMailNtfAddInfo();
                        addInfo.mailInfo = mail.toProtocol();
                        player.send(CommandId.CLI_NTF_MAIL_ADD, addInfo);
                    }
                    Logs.MAIL.debug("sendSystemMailWithServer setp:%d", step);
                    if (++step >= 1000) {
                        Thread.yield();
                        step = 0;
                    }
                }
                Logs.MAIL.debug("end sendSystemMailWithServer setp:%d", step);
            }
        });
        return true;
    }

    public boolean sendSystemMail(long receivePlayerUid, String title, String content, int itemId, int num) {
        Player player = PlayerManager.I.getPlayer(receivePlayerUid);
        if (null == player) {
            Logs.MAIL.warn("%s 接收者不存在, 推送失败", receivePlayerUid);
            return false;
        }
        Mail mail = new Mail();
        mail.setUid(UIDManager.I.getAndInc(UIDType.MAIL));
        mail.setSenderPlayerUid(-1);
        mail.setReceivePlayerUid(receivePlayerUid);
        mail.setTitle(title);
        mail.setContent(content);
        mail.setState(0);
        mail.setSendTime(System.currentTimeMillis());
        if (num > 0 && itemId > EMoneyType.NORMAL.getValue() && itemId <= EMoneyType.TICKET.getValue()) {
            mail.getItem().put(itemId, num);
            mail.setItemState(1);
        }
        // TODO save
        DBManager.I.save(new Task() {
            @Override
            public void run() {
                DBManager.I.getMailDao().save(mail);
            }
        });
        if (player.isOnline()) {
            PCLIMailNtfAddInfo addInfo = new PCLIMailNtfAddInfo();
            addInfo.mailInfo = mail.toProtocol();
            player.send(CommandId.CLI_NTF_MAIL_ADD, addInfo);
        }
        return true;
    }

    //    public void pushToGroup(long groupUid, int type, String message) {
//        IClub club = ClubManager.I.getClubByUid(groupUid);
//        if (null == club) {
//            return;
//        }
//        List<Long> allPlayerUid = club.getAllMemberUids();
//        int step = 0;
//        Logs.MAIL.debug("begin push to group step:%d", step);
//        for (int i = 0, len = allPlayerUid.size(); i < len; ++i) {
//            long playerUid = allPlayerUid.get(i);
//            Player player = PlayerManager.I.getPlayer(playerUid);
//            if (null == player) {
//                Logs.MAIL.warn("%s 接收者不存在, 推送失败", playerUid);
//                continue;
//            }
//            // TODO save
//            XPushManager.I.push(String.valueOf(playerUid), "系统", type, message);
//            Logs.MAIL.debug("push to group step:%d", step);
//            if (++step >= 1000) {
//                Thread.yield();
//                step = 0;
//            }
//        }
//        Logs.MAIL.debug("end push to group step:%d", step);
//    }

//    public void XPushManager(int type, String message) {
//        List<Long> allPlayerUid = DBManager.I.getPlayerDao().loadAllUid();
//        int step = 0;
//        Logs.MAIL.debug("begin push to server step:%d", step);
//        Iterator<Long> it = allPlayerUid.iterator();
//        while (it.hasNext()) {
//            long playerUid = it.next();
//            Player player = PlayerManager.I.getPlayer(playerUid);
//            if (null == player) {
//                Logs.MAIL.warn("%s 接收者不存在, 发送邮件失败", playerUid);
//                continue;
//            }
//            // TODO save
//            XPushManager.I.push(String.valueOf(playerUid), "系统", type, message);
//            Logs.MAIL.debug("push to server step:%d", step);
//            if (++step >= 1000) {
//                Thread.yield();
//                step = 0;
//            }
//        }
//        Logs.MAIL.debug("end push to server step:%d", step);
//    }



    @Override
    public int save() {
        return 0;
    }

    @Override
    public int shutdown() {
        return 0;
    }
}
