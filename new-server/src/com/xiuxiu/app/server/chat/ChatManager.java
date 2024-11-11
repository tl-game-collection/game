package com.xiuxiu.app.server.chat;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.box.PCLIBoxNtfScoreInfo;
import com.xiuxiu.app.protocol.client.chat.PCLIChatMsg;
import com.xiuxiu.app.protocol.client.chat.PCLIGroupChatMsg;
import com.xiuxiu.app.server.BaseManager;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.KeyFilterManager;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.constant.SystemMessageConstant;
import com.xiuxiu.app.server.constant.SystemTipMessageConstant;
import com.xiuxiu.app.server.db.UIDManager;
import com.xiuxiu.app.server.db.UIDType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.ERoomType;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.app.server.score.BoxRoomScore;
import com.xiuxiu.core.ICallback;
import com.xiuxiu.core.utils.AsyncTask;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.RandomUtil;
import com.xiuxiu.core.utils.StringUtil;
import com.xiuxiu.core.utils.TimeUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatManager extends BaseManager {
    private static class ChatManagerHolder {
        private static ChatManager instance = new ChatManager();
    }

    public static ChatManager I = ChatManagerHolder.instance;

    private ChatManager() {
    }

    public long chat(Player player, byte type, long toUid, byte contentType, String content, long leagueUid,long gruopUid,long toGroupUid) {
        if (contentType < 0 || contentType >= EChatContentType.values().length) {
            Logs.CHAT.warn("%s say message:%s invalid contentTyp=[%d, %d)", player, content, 0, EChatContentType.values().length);
            return -1;
        }
        // 分配客服
        if (4 == type && toUid <= 0) {
            Player service = CustomerServiceManager.I.getCustomerService(player);
            toUid = null == service ? -1 : service.getUid();
        } else if (6 == type && toUid <= 0) {
            List<Player> plists = null;
            for (long i = Constant.PLATFORM_FINANCE_UID_BEGIN; i <= Constant.PLATFORM_RECHARGE_FINANCE_END; i++) {
                Player p = PlayerManager.I.getOnlinePlayer(i);
                if (p != null) {
                    plists = new ArrayList<>();
                    plists.add(p);
                }
            }
            if (plists == null) {
                return -1;
            }
            int index = RandomUtil.random(plists.size());
            toUid = plists.get(index).getUid();
        } else if (1 == type
                && (CustomerServiceManager.I.isCustomerService(toUid) || CustomerServiceManager.I.isCustomerService(player.getUid()))) {
            type = 4;
        } else if (1 == type && (toUid >= Constant.PLATFORM_FINANCE_UID_BEGIN && toUid <= Constant.PLATFORM_FINANCE_UID_END)) {
            type = 6;
        }

        if (StringUtil.isEmptyOrNull(content) || content.length() >= 4096) {
            Logs.CHAT.warn("%s say message invalid", player, content);
            return -1;
        }
        if (3 == type) {
            // 房间
            Room room = RoomManager.I.getRoom(player.getRoomId());
            if (null == room) {
                Logs.CHAT.warn("%s 房间roomId;%s 不存在, 聊天请求失败", player, player.getRoomId());
                return -1;
            }
            PCLIChatMsg chatMsg = new PCLIChatMsg();
            chatMsg.messageUid = UIDManager.I.getAndInc(UIDType.CHAT);
            chatMsg.sayTime = System.currentTimeMillis();
            chatMsg.messageType = type;
            chatMsg.message = content;
            chatMsg.contentType = contentType;
            chatMsg.fromPlayerUid = player.getUid();
            chatMsg.fromPlayerName = player.getName();
            chatMsg.fromPlayerIcon = player.getIcon();
            chatMsg.fromPlayerSex = player.getSex();

            if (0 == contentType && !StringUtil.isEmptyOrNull(content)) {
                chatMsg.message = KeyFilterManager.I.replace(content, '*');
            }

            room.broadcast2Client(CommandId.CLI_NTF_CHAT_MSG, chatMsg);
            return 0;
        } else {
            if (4 != type && 0 == contentType && !StringUtil.isEmptyOrNull(content)) {
                content = KeyFilterManager.I.replace(content, '*');
            }
            if (1 == type || 4 == type || 6 == type) { // 好友或者客服
                // if (1 == type && !player.getFriend().isFriend(toUid)) {
                // Logs.CHAT.warn("%s toUid;%s 不是你好友, 聊天请求失败", player, toUid);
                // return -1;
                // }
                // TODO 消息存储
                Player toPlayer = PlayerManager.I.getPlayer(toUid);
                if (null == toPlayer) {
                    Logs.CHAT.warn("%s toUid;%s 玩家不存在, 聊天请求失败", player, toUid);
                    return -1;
                }

                final long mailUid = UIDManager.I.getAndInc(UIDType.CHAT);
                final long sayTime = System.currentTimeMillis();
                //收消息的人
                MailBox mailBox = new MailBox();
                mailBox.setUid(UIDManager.I.getAndInc(UIDType.MAILBOX));
                mailBox.setMessageUid(mailUid);
                mailBox.setSayTime(sayTime);
                mailBox.setMessageType(type);
                mailBox.setMessage(content);
                mailBox.setContentType(contentType);
                mailBox.setFromPlayerUid(player.getUid());
                mailBox.setFromPlayerName(player.getName());
                mailBox.setToPlayerUid(toPlayer.getUid());
                mailBox.setTagPlayerUid(toPlayer.getUid());
                mailBox.setFromLeagueUid(leagueUid);
                mailBox.setFromGroupUid(gruopUid);
                mailBox.setToGroupUid(toGroupUid);
                if (gruopUid > 0 && toGroupUid > 0) {
                    mailBox.setFromGroupUid(toGroupUid);
                    mailBox.setToGroupUid(gruopUid);
                }
                MailBoxManager.I.post(mailBox);
                //发消息的人
                mailBox = new MailBox();
                mailBox.setUid(UIDManager.I.getAndInc(UIDType.MAILBOX));
                mailBox.setMessageUid(mailUid);
                mailBox.setSayTime(sayTime);
                mailBox.setMessageType(type);
                mailBox.setMessage(content);
                mailBox.setContentType(contentType);
                mailBox.setFromPlayerUid(player.getUid());
                mailBox.setFromPlayerName(player.getName());
                mailBox.setToPlayerUid(player.getUid());
                mailBox.setTagPlayerUid(toPlayer.getUid());
                mailBox.setFromLeagueUid(leagueUid);
                mailBox.setFromGroupUid(gruopUid);
                mailBox.setToGroupUid(toGroupUid);
                if (gruopUid > 0 && toGroupUid > 0) {
                    mailBox.setFromGroupUid(toGroupUid);
                    mailBox.setToGroupUid(gruopUid);
                }
                MailBoxManager.I.post(mailBox);
                return mailBox.getMessageUidByPlayer();
            }
            if (2 == type) {
                if (!player.hasClub(toUid)) {
                    Logs.CHAT.warn("%s 你不在groupUid;%s 这个群里, 聊天请求失败", player, toUid);
                    return -1;
                }
                IClub club = ClubManager.I.getClubByUid(toUid);
                if (null == club) {
                    Logs.CHAT.warn("%s 群groupUid;%s 不存在, 聊天请求失败", player, toUid);
                    return -1;
                }
                return this.send2Group(player, club, type, contentType, content);
            }
            if (7 == type) {
                IClub club = ClubManager.I.getClubByUid(toUid);
                if (club == null) {
                    Logs.CHAT.warn("%s 群clubUid;%s 不存在, 聊天请求失败", player, toUid);
                    return -1;
                }
                return this.send2Club(player, club, type, contentType, content);
            }
        }
        return -1;
    }

    // 系统通知群客服
    public void send2GroupServiceBySystem(final Player player, final Player chief, final Player service, IClub club) {
        final long joinTime = System.currentTimeMillis();
        PCLIGroupChatMsg chatMsg = new PCLIGroupChatMsg();
        chatMsg.joinTime = joinTime;
        chatMsg.playerUid = null == player ? -1 : player.getUid();
        chatMsg.playerName = null == player ? "" : player.getName();
        chatMsg.groupUid = club.getClubUid();
        if (null != chief) {
            chief.send(CommandId.CLI_NTF_GROUP_CHAT_MSG, chatMsg);
        }
        if (null != service) {
            service.send(CommandId.CLI_NTF_GROUP_CHAT_MSG, chatMsg);
        }
    }

    public long send2Club(final Player player, final IClub club, final byte type, final byte contentType, final String content,
                           final Object... args) {
        final long mailUid = UIDManager.I.getAndInc(UIDType.CHAT);
        final long sayTime = System.currentTimeMillis();
        final long[] msgUid = new long[1];

        club.foreach(new ICallback<ClubMember>() {
            @Override
            public void call(ClubMember... member) {
                MailBox mailBox = new MailBox();
                mailBox.setUid(UIDManager.I.getAndInc(UIDType.MAILBOX));
                mailBox.setMessageUid(mailUid);
                mailBox.setSayTime(sayTime);
                mailBox.setMessageType(type);
                mailBox.setMessage(content);
                mailBox.setContentType(contentType);
                mailBox.setFromPlayerUid(null == player ? -1 : player.getUid());
                mailBox.setFromGroupUid(club.getClubUid());
                if (null != args && args.length > 0) {
                    mailBox.setParam(Arrays.asList(args));
                }
                mailBox.setToPlayerUid(member[0].getPlayerUid());
                MailBoxManager.I.post(mailBox);
//                if (null != player) {
//                    msgUid[0] = mailBox.getMessageUidByPlayer();
//                }
                if (null != player && member[0].getPlayerUid() == player.getUid()) {
                    msgUid[0] = mailBox.getMessageUidByPlayer();
                }
            }
        });
        return msgUid[0];
    }

    public long send2Group(final Player player, final IClub club, final byte type, final byte contentType, final String content,
                           final Object... args) {
        // this.send2GroupBySystem(player, group, type, contentType, content,
        // args);
//        long mailUid = UIDManager.I.getAndInc(UIDType.CHAT);
//        long sayTime = System.currentTimeMillis();
//        ConcurrentHashMap<Long, Player> allPlayer = PlayerManager.I.getOnlinePlayer();
//        long groupUid = group.getUid();
//        Iterator<Map.Entry<Long, Player>> it = allPlayer.entrySet().iterator();
//        while (it.hasNext()) {
//            Player tempPlayer = it.next().getValue();
//            if (null != tempPlayer && tempPlayer.hasGroup(groupUid)) {
//                PCLIChatMsg chatMsg = new PCLIChatMsg();
//                chatMsg.messageUid = mailUid;
//                chatMsg.sayTime = sayTime;
//                chatMsg.messageType = type;
//                chatMsg.message = String.valueOf(content);
//                chatMsg.contentType = contentType;
//                chatMsg.tagPlayerUid = -1;
//                chatMsg.fromPlayerUid = null == player ? -1 : player.getUid();
//                chatMsg.fromPlayerName = null == player ? "" : player.getName();
//                chatMsg.fromPlayerIcon = null == player ? "" : player.getIcon();
//                chatMsg.fromGroupUid = group.getUid();
//                if (null != args && args.length > 0) {
//                    chatMsg.param = Arrays.asList(args);
//                }
//                tempPlayer.send(CommandId.CLI_NTF_CHAT_MSG, chatMsg);
//            }
//        }
//        return mailUid;
        final long mailUid = UIDManager.I.getAndInc(UIDType.CHAT);
        final long sayTime = System.currentTimeMillis();
        final long[] msgUid = new long[1];

        club.foreach(new ICallback<ClubMember>() {
            @Override
            public void call(ClubMember... member) {
                MailBox mailBox = new MailBox();
                mailBox.setUid(UIDManager.I.getAndInc(UIDType.MAILBOX));
                mailBox.setMessageUid(mailUid);
                mailBox.setSayTime(sayTime);
                mailBox.setMessageType(type);
                mailBox.setMessage(content);
                mailBox.setContentType(contentType);
                mailBox.setFromPlayerUid(null == player ? -1 : player.getUid());
                mailBox.setFromGroupUid(club.getClubUid());
                if (null != args && args.length > 0) {
                    mailBox.setParam(Arrays.asList(args));
                }
                mailBox.setToPlayerUid(member[0].getPlayerUid());
                MailBoxManager.I.post(mailBox);
                if (null != player && member[0].getPlayerUid() == player.getUid()) {
                    msgUid[0] = mailBox.getMessageUidByPlayer();
                }
            }
        });
        return msgUid[0];
    }

    public long send2GroupWithMemberUid(final Player player, final IClub club, final byte type, final byte contentType,
                                        final String content, final long memberUid, final Object... args) {
        ClubMember memberInfo = club.getMember(memberUid);
        if (null == memberInfo) {
            return -1;
        }
        final long mailUid = UIDManager.I.getAndInc(UIDType.CHAT);
        final long sayTime = System.currentTimeMillis();
        long msgUid = -1;
        MailBox mailBox = new MailBox();
        mailBox.setUid(UIDManager.I.getAndInc(UIDType.MAILBOX));
        mailBox.setMessageUid(mailUid);
        mailBox.setSayTime(sayTime);
        mailBox.setMessageType(type);
        mailBox.setMessage(content);
        mailBox.setContentType(contentType);
        mailBox.setFromPlayerUid(null == player ? -1 : player.getUid());
        mailBox.setFromPlayerName(null == player ? "" : player.getName());
        mailBox.setFromGroupUid(club.getClubUid());
        if (null != args && args.length > 0) {
            mailBox.setParam(Arrays.asList(args));
        }
        mailBox.setToPlayerUid(memberUid);
        MailBoxManager.I.post(mailBox);
        if (null != player && memberUid == player.getUid()) {
            msgUid = mailBox.getMessageUidByPlayer();
        }
        return msgUid;
    }

    protected void send2GroupBySystem(final Player player, final IClub club, final byte type, final byte contentType, final String content,
                                      final Object... args) {
        final long mailUid = UIDManager.I.getAndInc(UIDType.CHAT);
        final long sayTime = System.currentTimeMillis();
        club.foreach(new ICallback<ClubMember>() {
            @Override
            public void call(ClubMember... member) {
                Player temp = PlayerManager.I.getOnlinePlayer(member[0].getUid());
                if (null == temp) {
                    return;
                }
                PCLIChatMsg chatMsg = new PCLIChatMsg();
                chatMsg.messageUid = mailUid;
                chatMsg.sayTime = sayTime;
                chatMsg.messageType = type;
                chatMsg.message = String.valueOf(content);
                chatMsg.contentType = contentType;
                chatMsg.tagPlayerUid = -1;
                chatMsg.fromPlayerUid = null == player ? -1 : player.getUid();
                chatMsg.fromPlayerName = null == player ? "" : player.getName();
                chatMsg.fromPlayerIcon = null == player ? "" : player.getIcon();
                chatMsg.fromGroupUid = club.getClubUid();
                if (null != args && args.length > 0) {
                    chatMsg.param = Arrays.asList(args);
                }
                temp.send(CommandId.CLI_NTF_CHAT_MSG, chatMsg);
            }
        });
    }

    public void notifyRoomDissolveWithOwner(long playerUid, long gameType, long gameSubType, int curBureau, int bureau, int money,
                                            int roomId) {
        this.systemMessage(playerUid, SystemMessageConstant.ROOM_DISSOLVE_OWNER, gameType, gameSubType, curBureau, bureau, money, roomId);
    }

    public void notifyRoomDissolve(long playerUid, long gameType, long gameSubType, int roomId) {
        this.systemMessage(playerUid, SystemMessageConstant.ROOM_DISSOLVE, gameType, gameSubType, roomId);
    }

    public void notifyArenaBalance(long playerUid, long gameType, long gameSubType, String groupName, long arenaUid) {
        this.systemMessage(playerUid, SystemMessageConstant.ARENA_BALANCE, gameType, gameSubType, groupName, arenaUid);
    }

    public void notifyDiamondNotEnough(long playerUid, float diamond) {
        this.systemMessage(playerUid, SystemMessageConstant.DIAMOND_NOT_ENOUGH, diamond);
    }

    public void notifyGroupDissolve(long playerUid, String groupIcon, String groupName, long groupUid) {
        this.systemMessage(playerUid, SystemMessageConstant.GROUP_DISSOLVE, groupIcon, groupName, groupUid);
    }

    /**
     * 通知提现完成
     *
     * @param playerUid      玩家UID
     * @param withdrawType   提现方式类型：1-微信; 2-支付宝; 3-银行卡
     * @param account        提现账号
     * @param withdrawAmount 提现金额 单位:分
     * @param applyTime      申请提现时间
     * @param state          处理结果: 1-提现成功, 2-提现被拒绝
     * @param moneyType      提现货币类型: 1-钱包, 2-星币
     */
    public void notifyWithdrawFinished(long playerUid, int withdrawType, String account, long withdrawAmount, Timestamp applyTime,
                                       int state, int moneyType) {
        this.systemMessageWithdrawFinished(playerUid, SystemMessageConstant.WITHDRAW_FINISHED, withdrawType, account, withdrawAmount,
                applyTime, state, moneyType);
    }

    public void systemMessageWithdrawFinished(long playerUid, int sysMsgId, Object... args) {
        Player player = PlayerManager.I.getPlayer(playerUid);
        if (null == player) {
            return;
        }
        PCLIChatMsg chatMsg = new PCLIChatMsg();
        chatMsg.messageUid = MailBoxManager.I.getLastMsgUid(playerUid).lastMsgUidInc();
        chatMsg.sayTime = System.currentTimeMillis();
        chatMsg.messageType = 0;
        chatMsg.message = String.valueOf(sysMsgId);
        chatMsg.contentType = EChatContentType.TIP.ordinal();
        chatMsg.tagPlayerUid = -1;
        chatMsg.fromPlayerUid = -1;
        chatMsg.fromPlayerName = "";
        chatMsg.fromPlayerIcon = "";
        chatMsg.fromGroupUid = -1;
        chatMsg.param = Arrays.asList(args);
        player.send(CommandId.CLI_NTF_CHAT_MSG, chatMsg);
    }

    public void systemMessage(long playerUid, int sysMsgId, Object... args) {
        Player player = PlayerManager.I.getOnlinePlayer(playerUid);
        if (null == player) {
            return;
        }
        PCLIChatMsg chatMsg = new PCLIChatMsg();
        chatMsg.messageUid = MailBoxManager.I.getLastMsgUid(playerUid).lastMsgUidInc();
        chatMsg.sayTime = System.currentTimeMillis();
        chatMsg.messageType = 0;
        chatMsg.message = String.valueOf(sysMsgId);
        chatMsg.contentType = EChatContentType.TIP.ordinal();
        chatMsg.tagPlayerUid = -1;
        chatMsg.fromPlayerUid = -1;
        chatMsg.fromPlayerName = "";
        chatMsg.fromPlayerIcon = "";
        chatMsg.fromGroupUid = -1;
        chatMsg.param = Arrays.asList(args);
        player.send(CommandId.CLI_NTF_CHAT_MSG, chatMsg);
    }

    public void send2GroupByTip(long groupUid, int ownerType, String content, Object... args) {
//        AsyncTask.I.addTask(new Runnable() {
//            @Override
//            public void run() {
//                if (Arena.OWNER_GROUP == ownerType) {
//                    Group group = GroupManager.I.getGroupByUid(groupUid);
//                    if (null == group || group.hasSwitch(ESwitch.GROUP_JOIN_ARENA_TIP)) {
//                        return;
//                    }
//                    send2Group(null, group, (byte) 2, (byte) EChatContentType.TIP.ordinal(), content, args);
//                } else if (Arena.OWNER_LEAGUE == ownerType) {
//                    League league = LeagueManager.I.getLeagueByUid(groupUid);
//                    Long[] allGroupUid = league.getAllMemberUid();
//                    for (Long gUid : allGroupUid) {
//                        Group group = GroupManager.I.getGroupByUid(gUid);
//                        if (null == group || group.hasSwitch(ESwitch.GROUP_JOIN_ARENA_TIP)) {
//                            continue;
//                        }
//                        send2Group(null, group, (byte) 2, (byte) EChatContentType.TIP.ordinal(), content, args);
//                    }
//                }
//            }
//        });
    }

    public void notifyCreateRoom(Player player, Room room) {
        IClub club = ClubManager.I.getClubByUid(room.getGroupUid());
        if (null == club || club.getClubType().match(EClubType.GOLD) || club.getClubType().match(EClubType.CARD)) {
            return;
        }
        this.send2Group(player, club, (byte) 2, (byte) EChatContentType.OPEN_ROOM.ordinal(), JsonUtil.toJson(room.getRoomBriefInfo()));
    }

    public void notifyBoxRoomScore(long groupUid, BoxRoomScore score) {
        IClub club = ClubManager.I.getClubByUid(groupUid);
        if (null == club || club.getClubType().match(EClubType.GOLD)) {
            return;
        }
        AsyncTask.I.addTask(new Runnable() {
            @Override
            public void run() {
                PCLIBoxNtfScoreInfo.BoxRoomScoreRecord roomScoreRecord = new PCLIBoxNtfScoreInfo.BoxRoomScoreRecord();
                roomScoreRecord.uid = score.getUid();
                roomScoreRecord.groupUid = score.getGroupUid();
                roomScoreRecord.boxUid = score.getBoxUid();
                roomScoreRecord.beginTime = score.getBeginTime();
                roomScoreRecord.endTime = score.getEndTime();
                roomScoreRecord.roomUid = score.getRoomUid();
                roomScoreRecord.roomId = score.getRoomId();
                Room room = RoomManager.I.getRoom(score.getRoomId());
                roomScoreRecord.rule = room.getRule();
                roomScoreRecord.gameType = score.getGameType();
                roomScoreRecord.gameSubType = score.getGameSubType();
                roomScoreRecord.totalScore = score.getTotalScore().toProtocolScoreInfo(ERoomType.BOX);
                if (club.checkIsJoinInMainClub()) {
                    List<Long> isSended = new ArrayList<>();//存已经发过消息的clubUid，避免重复发送在同一个群
                    IClub rootClub = ClubManager.I.getClubByUid(club.getFinalClubId());
                    for (int i = 0; i < score.getPlayerUids().size(); i++) {
                        long clubUid = rootClub.getEnterFromClubUid(score.getPlayerUids().get(i));
                        IClub tempClub = ClubManager.I.getClubByUid(clubUid);
                        if (tempClub == null) {
                            continue;
                        }
                        if (isSended.contains(clubUid)) {
                            continue;
                        }
                        isSended.add(clubUid);
                        send2Group(null, tempClub, (byte) 7, (byte) EChatContentType.SHARE_BOX_SCORE.ordinal(), JsonUtil.toJson(roomScoreRecord));
                    }
                } else {
                    send2Group(null, club, (byte) 7, (byte) EChatContentType.SHARE_BOX_SCORE.ordinal(), JsonUtil.toJson(roomScoreRecord));
                }
        }});
    }

    public long notifyGroupMute(final Player player, final IClub club, final long mutePlayerUid, final long muteTime) {
        final long mailUid = UIDManager.I.getAndInc(UIDType.CHAT);
        final long sayTime = System.currentTimeMillis();
        final long[] msgUid = new long[1];
        final Player mutePlayer = PlayerManager.I.getPlayer(mutePlayerUid);
        club.foreach(new ICallback<ClubMember>() {
            @Override
            public void call(ClubMember... member) {
                MailBox mailBox = new MailBox();
                mailBox.setUid(UIDManager.I.getAndInc(UIDType.MAILBOX));
                mailBox.setMessageUid(mailUid);
                mailBox.setSayTime(sayTime);
                mailBox.setMessageType((byte) 2);
                if (member[0].getUid() == mutePlayerUid) {
                    mailBox.setMessage(
                            String.format(SystemTipMessageConstant.TIP_MUTE_SELF, player.getName(), TimeUtil.getTimeFormat(muteTime)));
                } else {
                    mailBox.setMessage(String.format(SystemTipMessageConstant.TIP_MUTE_OTHER,
                            null == mutePlayer ? "" : mutePlayer.getName(), player.getName(), TimeUtil.getTimeFormat(muteTime)));
                }
                mailBox.setContentType((byte) EChatContentType.TIP.ordinal());
                mailBox.setFromPlayerUid(-1);
                mailBox.setFromPlayerName("");
                mailBox.setFromGroupUid(club.getClubUid());
                mailBox.setToPlayerUid(member[0].getUid());
                MailBoxManager.I.post(mailBox);
                if (null != player && member[0].getUid() == player.getUid()) {
                    msgUid[0] = mailBox.getMessageUidByPlayer();
                }
            }
        });
        return msgUid[0];
    }

    @Override
    public int save() {
        return 0;
    }

    @Override
    public int shutdown() {
        return 0;
    }
}
