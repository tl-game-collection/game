package com.xiuxiu.app.server.player;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.fastjson.TypeReference;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.PCLIPlayerBriefInfo;
import com.xiuxiu.app.protocol.client.PCLIPlayerInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfDelInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfListInfo;
import com.xiuxiu.app.protocol.client.mail.PCLIMailNtfInfo;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfChangePropertyInfo;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfVisitCardInfo;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerSmallInfo;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.account.Account;
import com.xiuxiu.app.server.account.AccountManager;
import com.xiuxiu.app.server.account.EAccount;
import com.xiuxiu.app.server.box.IBoxOwner;
import com.xiuxiu.app.server.chat.ChatManager;
import com.xiuxiu.app.server.chat.CustomerServiceManager;
import com.xiuxiu.app.server.chat.MailBoxManager;
import com.xiuxiu.app.server.chat.MailBoxUid;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.constant.EMoneyType;
import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.app.server.db.UIDManager;
import com.xiuxiu.app.server.db.UIDType;
import com.xiuxiu.app.server.mail.Mail;
import com.xiuxiu.app.server.mail.MailManager;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.handle.IBoxRoomHandle;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.app.server.statistics.constant.EMoneyExpendRoomType;
import com.xiuxiu.app.server.statistics.constant.EMoneyExpendType;
import com.xiuxiu.app.server.statistics.consume.PlayerMoneyConsumeMonthData;
import com.xiuxiu.app.server.statistics.consume.PlayerMoneyConsumeRecord;
import com.xiuxiu.app.server.statistics.moneyrecord.MoneyExpendRecord;
import com.xiuxiu.app.server.system.AnnouncementManager;
import com.xiuxiu.core.ICallback;
import com.xiuxiu.core.ds.ConcurrentHashSet;
import com.xiuxiu.core.net.Connection;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.net.protocol.IErrorCode;
import com.xiuxiu.core.utils.AsyncTask;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.RandomUtil;
import com.xiuxiu.core.utils.StringUtil;
import com.xiuxiu.core.utils.TimeUtil;

public class Player extends BaseTable implements IPlayer {
    public static final long CHANGE_AVATAR_RECHARGE_LIMIT = 10000L;

    protected String name;
    protected String icon;
    protected String defaultIcon;
    protected String wechat;
    protected byte sex;
    protected String zone;
    protected int roomId = -1;
    protected long createTimestamp;
    protected long lastLoginTime;
    protected long lastLogoutTime;
    protected String lastLoginIp;
    protected double lat;                   // 纬度
    protected double lng;                   // 经度
    protected ConcurrentHashMap<EMoneyType, Number> money = new ConcurrentHashMap<>();
    protected RecommendInfo recommend = new RecommendInfo();
    protected List<VisitCardInfo> visitCard = new ArrayList<>();
    protected ConcurrentHashMap<Long, String> alias = new ConcurrentHashMap<>();
    protected ConcurrentHashSet<String> tags = new ConcurrentHashSet<>();
    protected ConcurrentHashMap<Long, Long> msgTopV2 = new ConcurrentHashMap<>();
    protected ConcurrentHashSet<Long> msgMute = new ConcurrentHashSet<>();
    protected ConcurrentHashSet<Long> clubUids = new ConcurrentHashSet<>();
    protected long born;                                                // 出生年月
    protected String signature;                                         // 个性签名
    protected byte emotion;                                             // 情感 0: 保密, 1: 单身, 2: 恋爱中, 3: 已婚, 4:  同性
    protected List<String> showImage = new ArrayList<>();               // 展示图片
    protected String cover;                                             // 封面图片
    protected int privilege;                                            // 权限
    protected int ownerClubCnt;                                        // 群数量
    protected AtomicLong recharge = new AtomicLong();                   // 充值
    protected int bizChannel;                                           // 业务渠道
    protected int isDoneGame;                                        // 是否完成过一轮游戏

    /**
     * 银行卡号
     */
    protected String bankCard;
    /**
     * 银行卡持卡人
     */
    protected String bankCardHolder;

    protected transient volatile Connection conn;
    protected transient ConcurrentHashMap<Long, Mail> allMail = new ConcurrentHashMap<>();
    protected transient volatile boolean asyncLoad = false;

    protected transient String remoteIp = "127.0.0.1";
    protected transient long lastNotifyDiamondNotEnoughTime = 0;

    protected volatile boolean isEmpower = false;//授权用户(用于显示其微信)

    transient protected int todayInviteCnt;                             // 每日已搜索玩家数量
    transient protected long lastSearchTime;                            // 上次搜索玩家时间
    
    public Player() {
        this.tableType = ETableType.TB_PLAYER;
    }

    public void init() {
        if (StringUtil.isEmptyOrNull(this.defaultIcon)) {
            this.defaultIcon = String.valueOf(RandomUtil.random(1, 4));
        }

        if (StringUtil.isEmptyOrNull(this.wechat)) {
            Account account = AccountManager.I.getAccountByUid(this.uid);
            if (account != null && account.getType() == 3) {
                this.wechat = account.getName();
                this.dirty = true;
            }
        }
        if (!StringUtil.isEmptyOrNull(this.cover)) {
            if (this.cover.startsWith("http://download.file.305869.com/")) {
                this.cover = this.cover.replace("http://download.file.305869.com/", Config.FILE_DOWNLOAD_SERVER_URL);
                this.dirty = true;
            } else if (this.cover.startsWith("http://download.file.bjqyou.com/")) {
                this.cover = this.cover.replace("http://download.file.bjqyou.com/", Config.FILE_DOWNLOAD_SERVER_URL);
                this.dirty = true;
            } else if (this.cover.startsWith("http://download.file.544118.com/")) {
                this.cover = "";
                this.dirty = true;
            }
        }

        if (!StringUtil.isEmptyOrNull(this.icon)) {
            if (this.icon.startsWith("http://download.file.305869.com/")) {
                this.icon = this.icon.replace("http://download.file.305869.com/", Config.FILE_DOWNLOAD_SERVER_URL);
                this.dirty = true;
            } else if (this.icon.startsWith("http://download.file.bjqyou.com/")) {
                this.icon = this.icon.replace("http://download.file.bjqyou.com/", Config.FILE_DOWNLOAD_SERVER_URL);
                this.dirty = true;
            } else if (this.icon.startsWith("http://download.file.544118.com/")) {
                this.icon = "";
                this.dirty = true;
            }
        }

        synchronized (this.visitCard) {
            Iterator<VisitCardInfo> it = this.visitCard.iterator();
            while (it.hasNext()) {
                VisitCardInfo temp = it.next();
                String imgUrl = temp.getImgUrl();
                if (!StringUtil.isEmptyOrNull(imgUrl)) {
                    if (imgUrl.startsWith("http://download.file.305869.com/")) {
                        imgUrl = imgUrl.substring("http://download.file.305869.com/".length());
                        temp.setImgUrl(imgUrl);
                        this.dirty = true;
                    } else if (imgUrl.startsWith("http://download.file.bjqyou.com/")) {
                        imgUrl = imgUrl.substring("http://download.file.bjqyou.com/".length());
                        temp.setImgUrl(imgUrl);
                        this.dirty = true;
                    } else if (imgUrl.startsWith("http://download.file.544118.com/")) {
                        it.remove();
                        this.dirty = true;
                    }
                }
            }
        }
        synchronized (this.showImage) {
            for (int i = 0, len = this.showImage.size(); i < len; ++i) {
                String imgUrl = this.showImage.get(i);
                if (!StringUtil.isEmptyOrNull(imgUrl)) {
                    if (imgUrl.startsWith("http://download.file.305869.com/")) {
                        imgUrl = imgUrl.substring("http://download.file.305869.com/".length());
                        this.showImage.set(i, imgUrl);
                        this.dirty = true;
                    } else if (imgUrl.startsWith("http://download.file.bjqyou.com/")) {
                        imgUrl = imgUrl.substring("http://download.file.bjqyou.com/".length());
                        this.showImage.set(i, imgUrl);
                        this.dirty = true;
                    } else if (imgUrl.startsWith("http://download.file.544118.com/")) {
                        this.showImage.remove(i);
                        --i;
                        --len;
                        this.dirty = true;
                    }
                }
            }
        }
        if (this.dirty) {
            this.save();
        }
    }

    public void login() {
        Logs.PLAYER.debug("%s 登陆", this);

        if (-1 != this.roomId) {
            Room room = RoomManager.I.getRoom(this.roomId);
            if (null == room || null == room.getRoomPlayer(this.uid)) {
                this.roomId = -1;
            }
        }

        this.lastLoginTime = System.currentTimeMillis();
        this.lastLoginIp = this.conn.getRemoteAddr();
        this.dirty = true;

        this.sendMyPlayerInfo();
        this.sendClubListInfo();
        this.loginFinish();
        this.asyncLoad = false;
        this.loadAsync();
    }

    public void relogin() {
        Logs.PLAYER.debug("%s 重登", this);
        if (-1 != this.roomId) {
            Room room = RoomManager.I.getRoom(this.roomId);
            if (null == room || null == room.getRoomPlayer(this.uid)) {
                this.roomId = -1;
            }
        }

        this.lastLoginTime = System.currentTimeMillis();
        this.lastLoginIp = this.conn.getRemoteAddr();
        this.dirty = true;

        boolean hasWithoutRoom = false;
        if (!hasWithoutRoom && -1 != this.roomId) {
            RoomManager.I.online(this);
        }

        this.sendMyPlayerInfo();
        this.sendClubListInfo();

        this.loginFinish();
        this.loadAsync();
    }

    public void logout(boolean save) {
        Logs.PLAYER.debug("%s 登出", this);
        this.lastLogoutTime = System.currentTimeMillis();
        // TODO 下线先离开
        boolean hasWithoutRoom = false;
        if (!hasWithoutRoom && -1 != this.roomId) {
            RoomManager.I.offline(this);
        }
        CustomerServiceManager.I.offline(this.getUid());
        this.conn.close();
        this.dirty = true;
        if (save) {
            this.save();
        }
    }

    @Override
    public boolean isOnline() {
        //return PlayerManager.I.isOnline(this.getUid());
        return this.lastLoginTime >= this.lastLogoutTime;
    }

    public void addClub(IClub iClub) {
        this.clubUids.add(iClub.getClubUid());
        this.dirty = true;
        if (this.isOnline()) {
            this.send(CommandId.CLI_NTF_CLUB_ADD_INFO, iClub.getClubInfoPCL(this));
        } else {
            this.save();
        }
    }

    public void leaveClub(long clubUid) {
        if (this.clubUids.remove(clubUid)) {
            this.dirty = true;
            if (this.isOnline()) {
                PCLIClubNtfDelInfo delInfo = new PCLIClubNtfDelInfo();
                delInfo.clubUid = clubUid;
                this.send(CommandId.CLI_NTF_CLUB_DEL_INFO, delInfo);
            } else {
                this.save();
            }
        }
    }

    protected void loadAsync() {
        if (this.asyncLoad) {
            return;
        }
        this.asyncLoad = true;
        final Player self = this;
        // 异步加载
        AsyncTask.I.addTask(new Runnable() {
            @Override
            public void run() {
                // 邮件
                List<Mail> list = MailManager.I.getAllMailByPlayerUid(uid);
                PCLIMailNtfInfo mailNtfInfo = new PCLIMailNtfInfo();
                Iterator<Mail> it = list.iterator();
                while (it.hasNext()) {
                    Mail mail = it.next();
                    mail = allMail.putIfAbsent(mail.getUid(), mail);
                    mailNtfInfo.list.add(mail.toProtocol());
                }
                send(CommandId.CLI_NTF_MAIL_INFO, mailNtfInfo);

                // 聊天记录
                MailBoxUid mailBoxUid = MailBoxManager.I.getLastMsgUid(uid);
                mailBoxUid.sendLoadMailBox(self);
                Logs.PLAYER.warn(" player uid %d mailBoxUid %d - %d",self.getUid(),mailBoxUid.getLastMsgUid(),mailBoxUid.getLastMsgUidByClient());
                CustomerServiceManager.I.online(uid);
                AnnouncementManager.I.postAnnouncementsIfNeeded(self);
                asyncLoad = false;
            }
        });
    }

    public Mail getMail(long mailUid) {
        return this.allMail.get(mailUid);
    }

    public void delMail(long mailUid) {
        this.allMail.remove(mailUid);
    }

    public List<Mail> delAllMailByReadAndReceive() {
        if (this.allMail.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<Mail> delList = new LinkedList<>();
        Iterator<Map.Entry<Long, Mail>> it = this.allMail.entrySet().iterator();
        while (it.hasNext()) {
            Mail mail = it.next().getValue();
            if (0 == mail.getState()) {
                continue;
            }
            if (1 == mail.getItemState()) {
                continue;
            }
            mail.setState(2);
            delList.add(mail);
            it.remove();
        }
        return delList;
    }

    public List<Mail> receiveAllMailItem() {
        if (this.allMail.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<Mail> receiveList = new LinkedList<>();
        Iterator<Map.Entry<Long, Mail>> it = this.allMail.entrySet().iterator();
        while (it.hasNext()) {
            Mail mail = it.next().getValue();
            if (1 == mail.getItemState()) {
                mail.setState(1);
                mail.setItemState(2);
                receiveList.add(mail);
                this.receiveMailItem(mail);
            }
        }
        return receiveList;
    }

    public void receiveMailItem(Mail mail) {
        for (Map.Entry<Integer, Integer> entry : mail.getItem().entrySet()) {
            int itemId = entry.getKey();
            int itemNum = entry.getValue();
            if (itemId < EMoneyType.values().length) {
//                this.addMoney(EMoneyType.values()[itemId], itemNum, this.uid);
        this.addMoney(EMoneyType.values()[itemId], itemNum, this.uid,-1,EMoneyExpendType.NORMAL,-1);
            }
        }
    }

    public PCLIPlayerSmallInfo getPlayerSmallInfo() {
        PCLIPlayerSmallInfo smallInfo = new PCLIPlayerSmallInfo();
        smallInfo.uid = this.uid;
        smallInfo.name = this.name;
        smallInfo.icon = this.getIcon();
        smallInfo.sex = this.sex;
        return smallInfo;
    }

    @Override
    public PCLIPlayerBriefInfo getPlayerBriefInfo(IPlayer player) {
        PCLIPlayerBriefInfo briefInfo = new PCLIPlayerBriefInfo();
        briefInfo.uid = this.uid;
        briefInfo.name = this.name;
        briefInfo.icon = this.getIcon();
        briefInfo.sex = this.sex;
        briefInfo.alias = null == player ? "" : player.getAlias(this.uid);
        briefInfo.zone = this.zone;
        // TODO tags
        //briefInfo.tags = null;
        briefInfo.lastLogoutTime = this.isOnline() ? -1 : this.lastLogoutTime;
        return briefInfo;
    }

    //
    public void sendMyPlayerInfo() {
        PCLIPlayerInfo info = new PCLIPlayerInfo();
        info.uid = this.uid;
        info.name = this.name;
        info.icon = this.getIcon();
        info.roomId = this.roomId;
        info.isDoneGame =this.isDoneGame;
        if (-1 != this.roomId) {
            IRoom room = RoomManager.I.getRoom(this.roomId);
            info.gameType = null == room ? -1 : room.getGameType();
            if (room.getRoomHandle() instanceof IBoxRoomHandle) {
                IBoxRoomHandle boxRoomHandle = (IBoxRoomHandle) room.getRoomHandle();
                info.boxId = boxRoomHandle.getBoxUid();
                info.clubUid = boxRoomHandle.getFromClubUid(this.uid);
            }
        } else {
            info.gameType = -1;
        }
        info.sex = this.sex;
        info.zone = this.zone;
        for (Map.Entry<EMoneyType, Number> entry : this.money.entrySet()) {
            info.money.put(entry.getKey().getValue(), entry.getValue());
        }
        info.born = this.born;
        info.signature = this.signature;
        info.emotion = this.emotion;
        this.addShowImageTo(info.showImage);
        info.cover = Config.FILE_DOWNLOAD_SERVER_URL + this.cover;
        info.privilege = EPlayerPrivilegeLevel.getAllPrivilege(this.privilege);
        info.wechat = this.wechat;
        Account account = AccountManager.I.getAccountByUid(this.uid);
        info.registerType = account.getType();
        if (account.getType() == EAccount.PHONE_LOGIN.getValue()
                || account.getType() == EAccount.RAPID_LOGIN.getValue()
                || account.getType() == EAccount.PWD_LOGIN.getValue()){
           info.registerParam = account.getPhone();
        }
        if (account.getType() == EAccount.DING_DING_LOGIN.getValue() || account.getType() == EAccount.XIAN_LIAO_LOGIN.getValue()){
            info.registerParam = account.getName();
        }
        info.bankCard = this.bankCard;
        info.bankCardHolder = this.bankCardHolder;
        info.phone = account.getPhone();
        this.send(CommandId.CLI_NTF_PLAYER_INFO, info);
    }

    public void sendClubListInfo() {
        PCLIClubNtfListInfo groupListInfo = new PCLIClubNtfListInfo();
        Iterator<Long> it = this.clubUids.iterator();
        while (it.hasNext()) {
            IClub iClub = ClubManager.I.getClubByUid(it.next());
            if (null == iClub) {
                it.remove();
                continue;
            }
            groupListInfo.list.add(iClub.getClubInfoPCL(this));
        }
        this.send(CommandId.CLI_NTF_CLUB_LIST_INFO, groupListInfo);
    }

    private void loginFinish() {
        this.send(CommandId.CLI_NTF_PLAYER_LOGIN_FINISH, null);
    }
    
    public void notifyChangeProperty(EPlayerProperty property, Number value) {
        PCLIPlayerNtfChangePropertyInfo info = new PCLIPlayerNtfChangePropertyInfo();
        info.property = property.getType();
        info.value = value;
        this.send(CommandId.CLI_NTF_PLAYER_CHANGE_PROPERTY, info);
    }

    @Override
    public boolean save() {
        return DBManager.I.update(this);
    }

    @Override
    public boolean send(int commandId, Object message) {
        Logs.CMD.debug("%s conn:%s send commandId:%s message:%s", this, this.conn, Integer.toString(commandId, 16), message);
        if (null == this.conn) {
            Logs.CMD.error("%s send commandId:%s message:%s FAIL", this, Integer.toString(commandId, 16), message);
            return false;
        }
        if (message instanceof ErrorCode) {
            message = new ErrorMsg((IErrorCode) message);
        }
        this.conn.send(commandId, message);
        return true;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        Logs.CMD.debug("%s conn:%s oldConn:%s new conn", this, conn, this.conn);
        this.conn = conn;
        this.remoteIp = conn.getRemoteIp();
    }

    @Override
    public long getUid() {
        return this.uid;
    }

    @Override
    public void setUid(long uid) {
        this.uid = uid;
    }

    @Override
    public String getName()  {
       return this.name;
    }

    public void setName(String name)  {
       this.name=name;
    }

    public void adjustName() {
        if (this.recharge.get() < CHANGE_AVATAR_RECHARGE_LIMIT) {
            if (!StringUtil.isEmptyOrNull(this.name)) {
                this.name = this.name.replaceAll("(?:([a-z]|([A-Z]|([0-9]))))", "*");
                this.dirty = true;
            }
        }
    }

    public ErrorCode changeName(String name) {
        this.name = name;
        this.dirty = true;
        if (!this.isOnline()) {
            this.save();
        }
        return ErrorCode.OK;
    }

    @Override
    public String getIcon() {
        if (StringUtil.isEmptyOrNull(this.icon)) {
            return this.defaultIcon;
        }
        return this.icon;
//        if (this.recharge.get() >= CHANGE_AVATAR_RECHARGE_LIMIT) {
//            if (StringUtil.isEmptyOrNull(this.icon)) {
//                return this.defaultIcon;
//            }
//            return this.icon;
//        }
//        return this.defaultIcon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIconDb() {
        return this.icon;
    }

    public void setIconDb(String icon) {
        this.icon = icon;
    }

    public ErrorCode changeIcon(String icon) {
        this.icon = icon;
        this.dirty = true;

        return ErrorCode.OK;
//        if (this.recharge.get() >= CHANGE_AVATAR_RECHARGE_LIMIT) {
//            this.icon = icon;
//            this.dirty = true;
//            return ErrorCode.OK;
//        }
//        return ErrorCode.PLAYER_NOT_CHANGE_ICON_0;
    }

    public String getDefaultIcon() {
        return defaultIcon;
    }

    public void setDefaultIcon(String defaultIcon) {
        this.defaultIcon = defaultIcon;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public ErrorCode changeWechat(String wechat) {
        if (this.wechat == null || !this.wechat.equals(wechat)) {
            this.wechat = wechat;
            this.dirty = true;
        }
        return ErrorCode.OK;
    }

    @Override
    public byte getSex() {
        return this.sex;
    }

    public void setSex(byte sex) {
        this.sex = sex;
    }

    public void changeSex(byte sex) {
        this.sex = sex;
        this.dirty = true;
    }

    public String getZone() {
        return this.zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public void changeZone(String zone) {
        this.zone = zone;
        this.dirty = true;
    }

    public int getRoomId() {
        return this.roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    @Override
    public synchronized void changeRoomId(int roomId, int gameType) {
        if (roomId == this.roomId) {
            return;
        }
        this.roomId = roomId;
        this.dirty = true;
        if (this.isOnline()) {
            this.notifyChangeProperty(EPlayerProperty.ROOM_UID, roomId);
            this.notifyChangeProperty(EPlayerProperty.GAME_TYPE, gameType);
        } else {
            this.save();
        }
    }

   

    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public long getLastLogoutTime() {
        return lastLogoutTime;
    }

    public void setLastLogoutTime(long lastLogoutTime) {
        this.lastLogoutTime = lastLogoutTime;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public String getLogngIp() {
        return this.lastLoginIp.substring(0, this.lastLoginIp.indexOf(":"));
    }

    public String getMoneyDb() {
        return JsonUtil.toJson(this.money);
    }

    public void setMoneyDb(String value) {
        if (StringUtil.isEmptyOrNull(value)) {
            return;
        }
        ConcurrentHashMap<EMoneyType, Number> temp = JsonUtil.fromJson(value, new TypeReference<ConcurrentHashMap<EMoneyType, Number>>() {
        });
        if (null != temp) {
            this.money = temp;
        }
    }

    @Override
    public boolean hasMoney(EMoneyType type, Number value) {
        return this.money.getOrDefault(type, 0).floatValue() >= value.floatValue();
    }

    public synchronized boolean addMoney(EMoneyType type, Number value, int reason, long now) {
        if (EMoneyType.DIAMOND == type) {
            float oldValue = this.money.getOrDefault(type, 0).floatValue();
            float newValue = oldValue + value.floatValue();
            if (newValue < 0) {
                if ((now - this.lastNotifyDiamondNotEnoughTime) > TimeUtil.HALF_HOUR_MS) {
                    if (EMoneyType.DIAMOND == type) {
                        this.lastNotifyDiamondNotEnoughTime = now;
                        ChatManager.I.notifyDiamondNotEnough(this.uid, oldValue);
                    }
                }
                return false;
            }
//            if (EMoneyType.WALLET == type) {
//                WalletManager.I.saveRecord(this.uid, reason, value.floatValue(), oldValue, now);
//            }
//            if (EMoneyType.LEAGUE == type) {
//                //LeagueManager.I.saveRecord(this.uid, reason, value, oldValue, now);
//            }
            this.money.put(type, newValue);
            this.dirty = true;
            if (this.isOnline()) {
                this.notifyChangeProperty(EPlayerProperty.values()[type.getValue()], newValue);
            } else {
                this.save();
            }
        } else {
            int oldValue = this.money.getOrDefault(type, 0).intValue();
            int newValue = oldValue + value.intValue();
            if (newValue < 0) {
                if ((now - this.lastNotifyDiamondNotEnoughTime) > TimeUtil.HALF_HOUR_MS) {
                    if (EMoneyType.DIAMOND == type) {
                        this.lastNotifyDiamondNotEnoughTime = now;
                        ChatManager.I.notifyDiamondNotEnough(this.uid, oldValue);
                    }
                }
                return false;
            }
            this.money.put(type, newValue);
            this.dirty = true;
            if (this.isOnline()) {
                this.notifyChangeProperty(EPlayerProperty.values()[type.getValue()], newValue);
            } else {
                this.save();
            }
        }
        return true;
    }

    @Override
    public synchronized boolean addMoney(EMoneyType type, Number value, long optPlayer, long fromUid, EMoneyExpendType expendType, long operatorUid) {
        if (EMoneyType.DIAMOND == type) {
            float oldValue = this.money.getOrDefault(type, 0f).floatValue();
            BigDecimal bignum1 = new BigDecimal(Float.toString(oldValue));
            BigDecimal bignum2 = new BigDecimal(Float.toString(value.floatValue()));
            BigDecimal bignum3 = null;
            bignum3 =  bignum1.add(bignum2);
            float newValue = bignum3.floatValue();//oldValue + value.floatValue();
            if (0 != optPlayer && newValue < 0 && value.floatValue() < 0) {
                long now = System.currentTimeMillis();
                if ((now - this.lastNotifyDiamondNotEnoughTime) > TimeUtil.HALF_HOUR_MS) {
                    if (EMoneyType.DIAMOND == type) {
                        this.lastNotifyDiamondNotEnoughTime = now;
                        ChatManager.I.notifyDiamondNotEnough(this.uid, oldValue);
                    }
                }
                return false;
            }
            if (type == EMoneyType.DIAMOND) {
                long now = System.currentTimeMillis();
                this.addMoneyExpendRecord(optPlayer, value.floatValue(), now, fromUid, expendType.getValue(), operatorUid);
                this.addPlayerMoneyConsumeRecord(optPlayer, value.floatValue(), now, expendType);
            }
            this.money.put(type, newValue);
            this.dirty = true;
            if (this.isOnline()) {
                this.notifyChangeProperty(EPlayerProperty.values()[type.getValue()], newValue);
            } else {
                this.save();
            }
        } else {
            int oldValue = this.money.getOrDefault(type, 0).intValue();
            int newValue = oldValue + value.intValue();
            if (0 != optPlayer && newValue < 0 && value.intValue() < 0) {
                long now = System.currentTimeMillis();
                if ((now - this.lastNotifyDiamondNotEnoughTime) > TimeUtil.HALF_HOUR_MS) {
                    if (EMoneyType.DIAMOND == type) {
                        this.lastNotifyDiamondNotEnoughTime = now;
                        ChatManager.I.notifyDiamondNotEnough(this.uid, oldValue);
                    }
                }
                return false;
            }
            this.money.put(type, newValue);
            this.dirty = true;
            if (this.isOnline()) {
                this.notifyChangeProperty(EPlayerProperty.values()[type.getValue()], newValue);
            } else {
                this.save();
            }
        }
        //Logs.PLAYER.warn("玩家 %d 增加钻石数 %d, 增加类型 %s, 操作人 %d", this.getUid(), value, expendType.getDesc(), optPlayer);
        return true;
    }
    
    /**
     * 添加玩家房卡消耗数量统计
     * @param optPlayer
     * @param value
     * @param time
     * @param expendType
     */
    private void addPlayerMoneyConsumeRecord(long optPlayer, float value, long time, EMoneyExpendType expendType) {
        if (expendType == EMoneyExpendType.LOBBY_EXPEND || expendType == EMoneyExpendType.LOBBY_EXPEND_RETURN || expendType == EMoneyExpendType.GROUP_EXPEND 
                || expendType == EMoneyExpendType.GROUP_EXPEND_RETURN || expendType == EMoneyExpendType.LEAGUE_EXPEND || expendType == EMoneyExpendType.LEAGUE_EXPEND_RETURN) {
            final PlayerMoneyConsumeRecord data = DBManager.I.getPlayerMoneyConsumeRecordDAO().load(optPlayer);
            if (data != null) {
                updatePlayerMoneyConsumeMonthData(data, value, time, expendType);
            } else {
                insertPlayerMoneyConsumeMonthData(optPlayer, value, time, expendType);
            }
        }
    }
    
    private void insertPlayerMoneyConsumeMonthData(long optPlayer, float value, long time, EMoneyExpendType expendType) {
        PlayerMoneyConsumeRecord addData = new PlayerMoneyConsumeRecord();
        addData.setUid(UIDManager.I.getAndInc(UIDType.PLAYER_MONEY_CONSUME_RECORD));
        addData.setPlayerUid(optPlayer);
        switch (expendType) {
        case LOBBY_EXPEND:
        case LOBBY_EXPEND_RETURN:
            addData.setValue1(addData.getValue1() + value);
            PlayerMoneyConsumeMonthData monthData1 = addData.getMonthValue1();
            monthData1.getMonthCount().add(addData.getValue1());
            monthData1.setTime(time);
            addData.setDirty(true);
            break;
        case GROUP_EXPEND:
        case GROUP_EXPEND_RETURN:
            addData.setValue2(addData.getValue2() + value);
            PlayerMoneyConsumeMonthData monthData2 = addData.getMonthValue2();
            monthData2.getMonthCount().add(addData.getValue2());
            monthData2.setTime(time);
            addData.setDirty(true);
            break;
        case LEAGUE_EXPEND:
        case LEAGUE_EXPEND_RETURN:
            addData.setValue3(addData.getValue3() + value);
            PlayerMoneyConsumeMonthData monthData3 = addData.getMonthValue3();
            monthData3.getMonthCount().add(addData.getValue3());
            monthData3.setTime(time);
            addData.setDirty(true);
            break;
        default:
            break;
        }
        savePlayerMoneyConsumeMonthData(addData);
    }
    
    private void updatePlayerMoneyConsumeMonthData(PlayerMoneyConsumeRecord data, float value, long time, EMoneyExpendType expendType) {
        switch (expendType) {
        case LOBBY_EXPEND:
        case LOBBY_EXPEND_RETURN:
            data.setValue1(data.getValue1() + value);
            PlayerMoneyConsumeMonthData monthData1 = data.getMonthValue1();
            if (!TimeUtil.isOnMonth(new Date(monthData1.getTime()), new Date(time))) {
                monthData1.getMonthCount().add(data.getValue1());
                monthData1.setTime(time);
                data.setDirty(true);
            }
            break;
            
        case GROUP_EXPEND:
        case GROUP_EXPEND_RETURN:
            data.setValue2(data.getValue2() + value);
            PlayerMoneyConsumeMonthData monthData2 = data.getMonthValue2();
            if (!TimeUtil.isOnMonth(new Date(monthData2.getTime()), new Date(time))) {
                monthData2.getMonthCount().add(data.getValue2());
                monthData2.setTime(time);
                data.setDirty(true);
            }
            break;
        case LEAGUE_EXPEND:
        case LEAGUE_EXPEND_RETURN:
            data.setValue3(data.getValue3() + value);
            PlayerMoneyConsumeMonthData monthData3 = data.getMonthValue3();
            if (!TimeUtil.isOnMonth(new Date(monthData3.getTime()), new Date(time))) {
                monthData3.getMonthCount().add(data.getValue3());
                monthData3.setTime(time);
                data.setDirty(true);
            }
            break;
        default:
            break;
        }
        savePlayerMoneyConsumeMonthData(data);
    }
    
    private void savePlayerMoneyConsumeMonthData(final PlayerMoneyConsumeRecord data) {
        if (data!= null) {
            data.save();
        }
    }

    private void addMoneyExpendRecord(long optPlayer, float value, long expendTime, long fromUid, int expendType, long operatorUid) {
        MoneyExpendRecord recordInfo = new MoneyExpendRecord();
        recordInfo.setUid(UIDManager.I.getAndInc(UIDType.MONEY_EXPEND_RECORD));
        recordInfo.setFromUid(fromUid);
        recordInfo.setPlayerUid(optPlayer);
        recordInfo.setOperatorUid(operatorUid);
        recordInfo.setValue(value);
        recordInfo.setExpendTime(expendTime);
        recordInfo.setExpendType(expendType);
        recordInfo.setCreateTime(TimeUtil.getZeroTimestamp(expendTime));
        if (expendType == EMoneyExpendType.LOBBY_EXPEND.getValue() || expendType == EMoneyExpendType.LOBBY_EXPEND_RETURN.getValue()){
            recordInfo.setRoomType(EMoneyExpendRoomType.LOBBY.getValue());
        }else if (expendType == EMoneyExpendType.GROUP_EXPEND.getValue() || expendType == EMoneyExpendType.GROUP_EXPEND_RETURN.getValue()){
            recordInfo.setRoomType(EMoneyExpendRoomType.GROUP.getValue());
        }else if (expendType == EMoneyExpendType.LEAGUE_EXPEND.getValue() || expendType == EMoneyExpendType.LEAGUE_EXPEND_RETURN.getValue()){
            recordInfo.setRoomType(EMoneyExpendRoomType.LEAGUE.getValue());
        }else {
            recordInfo.setRoomType(EMoneyExpendRoomType.NORMAL.getValue());
        }
        Room room = RoomManager.I.getRoom(this.getRoomId());
        if (room != null) {
            recordInfo.setGameType(room.getGameType());
        }
        recordInfo.setDirty(true);
        recordInfo.save();
    }

    @Override
    public int getMoneyByType(EMoneyType type) {
        return this.money.getOrDefault(type, 0).intValue();
    }

    public String getAlias() {
        return JsonUtil.toJson(this.alias);
    }

    @Override
    public String getAlias(long uid) {
        return this.alias.getOrDefault(uid, "");
    }

    public String getAlias(long uid, String defaultValue) {
        return this.alias.getOrDefault(uid, defaultValue);
    }

    public void setAlias(String alias) {
        this.alias = JsonUtil.fromJson(alias, new TypeReference<ConcurrentHashMap<Long, String>>() {
        });
    }

    public void changeAlias(long otherUid, String alias) {
        this.alias.put(otherUid, alias);
        this.dirty = true;
    }

    public String getTags() {
        return JsonUtil.toJson(this.tags);
    }

    public void setTags(String tags) {
        this.tags = JsonUtil.fromJson(tags, new TypeReference<ConcurrentHashSet<String>>() {
        });
    }

    public String getMsgTopV2() {
        return JsonUtil.toJson(this.msgTopV2);
    }

    public void setMsgTopV2(String msgTop) {
        if (StringUtil.isEmptyOrNull(msgTop)) {
            return;
        }

        ConcurrentHashMap<Long, Long> temp = JsonUtil.fromJson(msgTop, new TypeReference<ConcurrentHashMap<Long, Long>>() {
        });
        if (null != temp) {
            this.msgTopV2 = temp;
        }
    }

    public void changeMsgTop(long otherUid, boolean flag) {
        if (flag) {
            this.msgTopV2.put(otherUid, System.currentTimeMillis());
        } else {
            this.msgTopV2.remove(otherUid);
        }
        this.dirty = true;
    }

    public String getMsgMute() {
        return JsonUtil.toJson(this.msgMute);
    }

    public void setMsgMute(String msgMute) {
        this.msgMute = JsonUtil.fromJson(msgMute, new TypeReference<ConcurrentHashSet<Long>>() {
        });
    }

    public void changeMsgMute(long otherUid, boolean flag) {
        if (flag) {
            this.msgMute.add(otherUid);
        } else {
            this.msgMute.remove(otherUid);
        }
        this.dirty = true;
    }

    public boolean isMsgMute(long uid) {
        return this.msgMute.contains(uid);
    }

    public void forEachByClub(ICallback<Long> callback) {
        Iterator<Long> it = this.clubUids.iterator();
        while (it.hasNext()) {
            callback.call(it.next());
        }
    }

    public int isClub() {
        return this.clubUids.size();
    }

    public ConcurrentHashSet<Long> getAllClubUids() {
        return this.clubUids;
    }

    public String getClubUids() {
        return JsonUtil.toJson(this.clubUids);
    }

    public void setClubUids(String groups) {
        this.clubUids = JsonUtil.fromJson(groups, new TypeReference<ConcurrentHashSet<Long>>() {
        });
    }

    public boolean hasClub(long uid) {
        return this.clubUids.contains(uid);
    }

    public boolean isCommonClub(Player other) {
        Iterator<Long> it1 = this.clubUids.iterator();
        while (it1.hasNext()) {
            Long g1 = it1.next();
            Iterator<Long> it2 = other.clubUids.iterator();
            while (it2.hasNext()) {
                Long g2 = it2.next();
                if (g1.longValue() == g2.longValue()) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getRecommend() {
        return JsonUtil.toJson(this.recommend);
    }

    public void setRecommend(String recommend) {
        if (StringUtil.isEmptyOrNull(recommend)) {
            return;
        }
        this.recommend = JsonUtil.fromJson(recommend, RecommendInfo.class);
    }

    public RecommendInfo getRecommendInfo() {
        return this.recommend;
    }


    public void changeRecommendPlayerUid(long recommendPlayerUid) {
        synchronized (this.recommend) {
            this.recommend.recommendPlayerUid = recommendPlayerUid;
        }
        this.dirty = true;
        if (this.isOnline()) {
            this.notifyChangeProperty(EPlayerProperty.RECOMMEND_PLAYER_UID, recommendPlayerUid);
        } else {
            this.save();
        }
    }

    public void changeRecommendUid(long recommendUid) {
        synchronized (this.recommend) {
            this.recommend.recommendPlayerUid = recommendUid;
        }
        this.dirty = true;
        if (!this.isOnline()) {
            this.save();
        }
    }

    public void addRecommendDiamond(int value) {
        synchronized (this.recommend) {
            if (this.recommend.diamond + value > Constant.RECOMMEND_DIAMOND) {
                value = this.recommend.diamond - Constant.RECOMMEND_DIAMOND;
            }
            this.recommend.diamond += value;
            this.dirty = Boolean.TRUE;
            if (!this.isOnline()) {
                this.save();
            }
        }
//        this.addMoney(EMoneyType.DIAMOND, value, this.uid);
        this.addMoney(EMoneyType.DIAMOND, value, this.uid, -1, EMoneyExpendType.RECOMMEND , -1);
    }

    public void addRecommend() {
        synchronized (this.recommend) {
            ++this.recommend.num;
        }
        this.dirty = true;
        if (!this.isOnline()) {
            this.save();
        }
    }

    public boolean hasVisitCard() {
        return !this.visitCard.isEmpty();
    }

    public void visitCardTo(List<String> list) {
        synchronized (this.visitCard) {
            for (VisitCardInfo temp : this.visitCard) {
                list.add(Config.FILE_DOWNLOAD_SERVER_URL + temp.getImgUrl());
            }
        }
    }

    public String getVisitCard() {
        return JsonUtil.toJson(visitCard);
    }

    public void setVisitCard(String visitCard) {
        if (StringUtil.isEmptyOrNull(visitCard)) {
            return;
        }
        List<VisitCardInfo> temp = JsonUtil.fromJson(visitCard, new TypeReference<ArrayList<VisitCardInfo>>() {
        });
        if (null != temp) {
            this.visitCard = temp;
        }
    }

//    public long getRecommendUid() {
//        return recommendUid;
//    }
//
//    public void setRecommendUid(long recommendUid) {
//        this.recommendUid = recommendUid;
//        this.dirty = true;
//    }

    public PCLIPlayerNtfVisitCardInfo getVisitCardTo() {
        PCLIPlayerNtfVisitCardInfo info = new PCLIPlayerNtfVisitCardInfo();
        synchronized (this.visitCard) {
            for (VisitCardInfo temp : this.visitCard) {
                info.list.add(new PCLIPlayerNtfVisitCardInfo.VisitCardInfo(temp.getDesc(), Config.FILE_DOWNLOAD_SERVER_URL + temp.getImgUrl()));
            }
        }
        info.bankCard = this.bankCard;
        info.bankCardHolder = this.bankCardHolder;
        return info;
    }

    public void addVisitCard(String desc, String fileName, int index) {
        synchronized (this.visitCard) {
            if (-1 == index) {
                this.visitCard.add(new VisitCardInfo(desc, fileName));
            } else if (index >= 0) {
                if (index < this.visitCard.size()) {
                    this.visitCard.get(index).setDescAndImgUrl(desc, fileName);
                } else {
                    this.visitCard.add(new VisitCardInfo(desc, fileName));
                }
            }
        }
        this.dirty = true;
    }

    public String getRemoteIp() {
        return this.remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    @Override
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public long getBorn() {
        return born;
    }

    public void setBorn(long born) {
        this.born = born;
    }

    public void changeBorn(long born) {
        this.born = born;
        this.dirty = true;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void changeSignature(String signature) {
        this.signature = signature;
        this.dirty = true;
    }

    public byte getEmotion() {
        return emotion;
    }

    public void setEmotion(byte emotion) {
        this.emotion = emotion;
    }

    public void changeEmotion(byte emotion) {
        this.emotion = emotion;
    }

    public void addShowImage(String url) {
        synchronized (this.showImage) {
            this.showImage.add(url);
        }
        this.dirty = true;
    }

    public void delShowImage(int index) {
        if (index < 0 || index >= this.showImage.size()) {
            return;
        }
        synchronized (this.showImage) {
            this.showImage.remove(index);
        }
    }

    public void replaceShowImage(int index, String newUrl) {
        if (index < 0 || index >= this.showImage.size()) {
            return;
        }
        synchronized (this.showImage) {
            this.showImage.set(index, newUrl);
        }
    }

    public void exchangeShowImage(int fromIndex, int toIndex) {
        if (fromIndex < 0 || fromIndex >= this.showImage.size()) {
            return;
        }
        if (toIndex < 0 || toIndex >= this.showImage.size()) {
            return;
        }
        if (fromIndex == toIndex) {
            return;
        }
        synchronized (this.showImage) {
            String fromUrl = this.showImage.get(fromIndex);
            String toUrl = this.showImage.get(toIndex);
            this.showImage.set(fromIndex, toUrl);
            this.showImage.set(toIndex, fromUrl);
        }
    }

    public List<String> getShowImage() {
        return showImage;
    }

    public void addShowImageTo(List<String> list) {
        synchronized (this.showImage) {
            for (String url : this.showImage) {
                list.add(Config.FILE_DOWNLOAD_SERVER_URL + url);
            }
        }
    }

    public String getShowImageDb() {
        return JsonUtil.toJson(this.showImage);
    }

    public void setShowImageDb(String showImage) {
        if (StringUtil.isEmptyOrNull(showImage)) {
            return;
        }
        List<String> temp = JsonUtil.fromJson(showImage, new TypeReference<ArrayList<String>>() {
        });
        if (null != temp) {
            this.showImage = temp;
        }
    }

    public void changeCover(String cover) {
        this.cover = cover;
        this.dirty = true;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getPrivilege() {
        return privilege;
    }

    public void setPrivilege(int privilege) {
        this.privilege = privilege;
    }

    public boolean hasPrivilege(EPlayerPrivilege privilege) {
        if ((Switch.PLAYER_PRIVILEGE_VERIFY & privilege.getValue()) > 0) {
            return true;
        }
        return EPlayerPrivilegeLevel.getValue(this.privilege, privilege) > 0;
    }

    public synchronized void modifyPrivilege(int level, boolean force) {
        int index = 0;
        int curPrivilege = this.privilege;
        while (0 != level) {
            int temp = level & 0x000f;
            if (temp > 0) {
                if (force || (temp > (curPrivilege & 0x000f))) {
                    int bit = index * 4;
                    this.privilege &= (~(0x0f << bit));
                    this.privilege |= (temp << bit);
                }
            }
            level >>= 4;
            curPrivilege >>= 4;
            ++index;
        }
        this.dirty = true;
        if (this.isOnline()) {
            this.notifyChangeProperty(EPlayerProperty.PRIVILEGE, EPlayerPrivilegeLevel.getAllPrivilege(this.privilege));
        } else {
            this.save();
        }
    }

    public int getOwnerClubCnt() {
        return ownerClubCnt;
    }

    public void setOwnerClubCnt(int ownerGroupCnt) {
        this.ownerClubCnt = ownerGroupCnt;
    }

    public synchronized void addOwnerClubCnt(boolean add) {
        if (add) {
            ++this.ownerClubCnt;
        } else {
            --this.ownerClubCnt;
        }
        this.dirty = true;
        if (!this.isOnline()) {
            this.save();
        }
    }

    public HashSet<Long> getCommonClubUid(Player other) {
        HashSet<Long> temp = new HashSet<>(this.clubUids);
        temp.retainAll(other.clubUids);
        return temp;
    }

    public HashSet<Long> getCommonClubUid(List<Long> clubUids) {
        HashSet<Long> temp = new HashSet<>(this.clubUids);
        temp.retainAll(clubUids);
        return temp;
    }

    public void addRecharge(long value) {
        this.recharge.addAndGet(value);
        this.dirty = true;
        if (!this.isOnline()) {
            this.save();
        }
    }

    public long getRecharge() {
        return recharge.longValue();
    }

    public void setRecharge(long recharge) {
        this.recharge.set(recharge);
    }

    public int getBizChannel() {
        return bizChannel;
    }

    public void setBizChannel(int bizChannel) {
        this.bizChannel = bizChannel;
    }

    public boolean isDoneGame(EPlayerDone type){
        return (isDoneGame & type.getValue()) > 0;
    }

    public void setDone(EPlayerDone type, boolean isDone){
        if (isDone) {
            isDoneGame = isDoneGame | type.getValue();
        }else {
            if (isDoneGame(type)){
                isDoneGame = isDoneGame ^ type.getValue();
            }
        }
        this.dirty = true;
        if (!this.isOnline()) {
            this.save();
        }
    }

    public int getIsDoneGame() {
        return isDoneGame;
    }

    public void setIsDoneGame(int isDoneGame) {
        this.isDoneGame = isDoneGame;
    }

    public boolean isEmpower() {
        return isEmpower;
    }

    public void setEmpower(boolean isEmpower) {
        this.isEmpower = isEmpower;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getBankCardHolder() {
        return bankCardHolder;
    }

    public void setBankCardHolder(String bankCardHolder) {
        this.bankCardHolder = bankCardHolder;
    }

    public int getTodayInviteCnt() {
        return todayInviteCnt;
    }

    public void setTodayInviteCnt(int todayInviteCnt) {
        this.todayInviteCnt = todayInviteCnt;
    }

    public long getLastSearchTime() {
        return lastSearchTime;
    }

    public void setLastSearchTime(long lastSearchTime) {
        this.lastSearchTime = lastSearchTime;
    }

    @Override
    public String toString() {
        return String.format("Player[Uid:%d, Name:%s RoomId:%d Lng:%s Lat:%s ClubUids:%s bizChannel:%d]",
                this.uid, this.name, this.roomId, this.lng, this.lat, this.clubUids, this.bizChannel);
    }

    /**
     * 更新银行卡号相关信息
     * @param type
     * @param value
     * @return
     */
    public ErrorCode changeBankCard(Integer type, String value) {
        if (null == value) {
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        value = value.trim();
        if (type == 0) {
            if (value.length() > 0 && value.length() > 20) {
                return ErrorCode.REQUEST_INVALID_DATA;
            }
            this.bankCardHolder = value;
        } else {
            if (value.length() > 0 && (value.length() < 15 || value.length() > 19)) {
                return ErrorCode.REQUEST_INVALID_DATA;
            }
            this.bankCard = value;
        }
        this.dirty = true;
        return ErrorCode.OK;
    }
    
    /**
     * 获取该玩家创建所有圈的玩法桌数量
     * @return
     */
    public Integer getPlayDeskCount() {
        if (this.clubUids.isEmpty()) {
            return 0;
        }
        int count = 0;
        Iterator<Long> iter = this.clubUids.iterator();
        while (iter.hasNext()) {
            Long tempUid = iter.next();
            IClub club = ClubManager.I.getClubByUid(tempUid);
            if (null == club) {
                continue;
            }
            if (club.getOwnerId() == tempUid) {
                count += ((IBoxOwner)club).getBoxSize();
            }
        }
        return count;
    }

}
