package com.xiuxiu.app.server.room.handle.impl.hundred;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.collect.Maps;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfDeskInfo;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfDeskInfoByLhd;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfOverByLhd;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfReb;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfRebInfoByLhd;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfRecord;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfTouzhurenRebInfoByLhd;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredVipSeatInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.BoxManager;
import com.xiuxiu.app.server.box.IBoxOwner;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.constant.EMoneyType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.db.UIDManager;
import com.xiuxiu.app.server.db.UIDType;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.handle.AbstractBoxRoomHandle;
import com.xiuxiu.app.server.room.normal.EState;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.Hundred.EHundredArenaRebType;
import com.xiuxiu.app.server.room.normal.Hundred.EHundredGameState;
import com.xiuxiu.app.server.room.normal.Hundred.HundredBureauRecordInfo;
import com.xiuxiu.app.server.room.normal.Hundred.HundredLhdBanker;
import com.xiuxiu.app.server.room.normal.Hundred.HundredPlayerInfo;
import com.xiuxiu.app.server.room.normal.Hundred.HundredRebRecordInfo;
import com.xiuxiu.app.server.room.normal.Hundred.IHundredBanker;
import com.xiuxiu.app.server.room.player.IHundredPlayer;
import com.xiuxiu.app.server.room.player.poker.HundredLhdPlayer;
import com.xiuxiu.app.server.statistics.constant.EMoneyExpendType;
import com.xiuxiu.core.ds.ConcurrentHashSet;
import com.xiuxiu.core.net.Task;
import com.xiuxiu.core.utils.AsyncTask;
import com.xiuxiu.core.utils.NumberUtils;

/**
 * 抽象百人场实现类
 * 
 * @author Administrator
 *
 */
public abstract class AbstractHundredRoomHandle extends AbstractBoxRoomHandle implements IHundredHandle {

    /** 百人场游戏状态 */
    protected AtomicReference<EHundredGameState> gameState = new AtomicReference<>(EHundredGameState.INIT);

    /** 该百人场游戏桌子的所有房间玩家容器 */
    protected ConcurrentHashMap<Long, IHundredPlayer> allPlayer = new ConcurrentHashMap<>();
    /** 离开游戏桌子的已下注或庄家玩家id */
    protected ConcurrentHashSet<Long> offlinePlayerUid = new ConcurrentHashSet<>();
    /** 已下注的玩家id */
    protected ConcurrentHashSet<Long> allRebUid = new ConcurrentHashSet<>();
    /** 牌库 */
    protected CopyOnWriteArrayList<Byte> allCard = new CopyOnWriteArrayList<>();

    /** vip 座位 */
    protected long[] vipSeat = new long[8];
    /** vip座位坐下限制 */
    protected int vipSeatLimit = 0;

    /** 过期时间 */
    protected long expire = -1;
    /** 准备开始过期时间 */
    protected int readyBeginTime = 0;
    /** 下注过期时间 */
    protected int rebTime = 0;
    /** 开牌过期时间 */
    protected int openCardTime = 0;
    /** 结束过期时间 */
    protected int overTime = 0;

    /** 庄家 */
    protected IHundredBanker curBanker = null;
    /** 庄家uid */
    protected long bankerUid = 0;
    /** 上庄列表 */
    protected CopyOnWriteArrayList<IHundredBanker> bankerList = new CopyOnWriteArrayList<>();

    /** 可下注金额 */
    protected int remainRebValue = 0;
    /** 下注限制 */
    protected int rebLimit = 1000;
    /** 所有下注金额 */
    protected long allRebValue = 0;

    protected volatile int winIndex = -1;

    protected int playerCnt = 1; // 闲家个数
    protected HundredPlayerInfo[] allReb;

    /** 开局时间 */
    protected long curBureauBeginTime = -1;
    /** 结束时间 */
    protected long curBureauEndTime = -1;
    /** 每局清理牌 */
    protected boolean awayBureauClearCard = true;
    /** 大赢家 */
    protected long bigWinPlayerUid = -1;

    /** 抽水 */
    protected int costModelValue;

    /** 缓存容器的初始容量 */
    private static final int INITIAL_CAPACITY = 10;
    /** 缓存容器的最大容量 */
    protected int MAX_CACHE_SIZE = 70;
    /** 百人场下注记录,格式：Map<玩家id,LinkedHashMap<uid,日志>> */
    private Map<Long, LinkedHashMap<Long, HundredRebRecordInfo>> rebRecordInfoMap = Maps.newHashMap();
    /** 百人场对局记录 */
    private List<HundredBureauRecordInfo> bureauRecordInfoList = new ArrayList<>();
    /** 对局记录缓存容器的最大容量 */
    protected int MAX_BUREAU_CACHE_SIZE = 70;

    /** 上庄顺序 1.顺序 2.金额 3.联合 */
    protected int upBankerOrder = 1;
    protected int upBankerValue;
    protected int downBankerValue;
    /** 一次当庄最大局数 */
    protected int maxBureau = 10;
    protected int maxMultiple = 8;
    /** 记录每个下注区域投注人的下注额 key 下注区域下标 value 玩家下注金额 */
    protected int[] rebAllRecords;
    protected boolean needNoticeAllRecord = false;
    protected long nextNoticeTime = 0;
    /** 庄家剩余可下注金额 */
    protected int leftAreanVlaue;

    public AbstractHundredRoomHandle(IRoom room, Box box) {
        super(room, box);
    }

    @Override
    public void init() {
        this.upBankerValue = this.room.getRule().getOrDefault(RoomRule.RR_HUNDRED_UP_BANKER_VALUE, 0) * 100;
        this.downBankerValue = this.room.getRule().getOrDefault(RoomRule.RR_HUNDRED_DOWN_BANKER_VALUE, 0) * 100;
        this.upBankerOrder = this.room.getRule().getOrDefault(RoomRule.RR_HUNDRED_UPBANKERORDER, 1);
        this.maxBureau = this.room.getRule().getOrDefault(RoomRule.RR_HUNDRED_MAXBUREAU, 10);

        this.playerCnt = this.getRoom().getRule().getOrDefault(RoomRule.RR_HUNDRED_PLAYER_CNT, this.playerCnt) + 1;
        this.vipSeatLimit = this.getRoom().getRule().getOrDefault(RoomRule.RR_HUNDRED_VIP_SEAT_LIMIT, 0);
        this.costModelValue = this.getRoom().getRule().getOrDefault(RoomRule.RR_COSTMODEL_VALUE, 0);
        this.rebLimit = this.getRoom().getRule().getOrDefault(RoomRule.RR_HUNDRED_REB_LIMIT, 1000);
        this.allReb = new HundredPlayerInfo[this.playerCnt];
        for (int i = 0; i < this.playerCnt; ++i) {
            this.allReb[i] = new HundredPlayerInfo(i);
        }
    }

    @Override
    public ErrorCode join(IRoomPlayer roomPlayer) {
        roomPlayer.setRoom(room);
        this.offlinePlayerUid.remove(roomPlayer.getUid());
        roomPlayer.changeState(EState.ONLINE);
        this.allPlayer.putIfAbsent(roomPlayer.getUid(), (IHundredPlayer)roomPlayer);
        // 通知加入百人场
        this.notifyJoin(roomPlayer);
        return ErrorCode.OK;
    }

    /**
     * 通知加入百人场
     * 
     * @param roomPlayer
     */
    protected void notifyJoin(IRoomPlayer roomPlayer) {
        //  通知加入百人场
        Player bankerPlayer = null;
        if (null != this.curBanker) {
            bankerPlayer = PlayerManager.I.getPlayer(this.curBanker.getUid());
        }
        PCLIHundredNtfDeskInfoByLhd info = new PCLIHundredNtfDeskInfoByLhd();
        info.boxId = this.getBoxUid();
        info.roomId = this.getRoomId();
        info.groupUid = this.getRoom().getGroupUid();
        info.gameType = this.getRoom().getGameType();
        info.bankerPlayerUid = null == bankerPlayer ? -1 : bankerPlayer.getUid();
        info.bankerPlayerName = null == bankerPlayer ? "" : bankerPlayer.getName();
        info.bankerPlayerIcon = null == bankerPlayer ? "" : bankerPlayer.getIcon();
        info.bankerUid = null == bankerPlayer ? -1 : this.curBanker.getBankerUid();
        info.bankerValue = NumberUtils.get2Decimals(null == bankerPlayer ? 0 : this.curBanker.getValue());
        info.curBureau = null == bankerPlayer ? 0 : this.curBanker.getBureau();
        info.remainRebValue = null == bankerPlayer ? 0 : this.remainRebValue / 100;
        info.rule.putAll(this.getRoom().getRule());
        info.state = this.gameState.get().ordinal();
        info.ownerType = 1;  // 0 普通场 1 群竞技场 2 联盟竞技场
        this.fillVipSeatInfo(info);
        if (-1 != this.expire) {
            info.remainTime = (int) ((this.expire - System.currentTimeMillis()) / 1000);
        } else {
            info.remainTime = -1;
        }

        if (this.isStart() || this.isOver()) {
            for (int i = 0; i < this.playerCnt; ++i) {
                HundredPlayerInfo temp = this.allReb[i];
                PCLIHundredNtfDeskInfoByLhd.RebInfo rebInfo = new PCLIHundredNtfDeskInfoByLhd.RebInfo();
                if (this.isOpenCard() || this.isOver()) {
                    rebInfo.card.addAll(temp.getResultCards().isEmpty() ? temp.getCards() : temp.getResultCards());
                    rebInfo.cardType = temp.getCardType().getValue();
                    rebInfo.win = temp.isWin(EHundredArenaRebType.PLAYER_WIN);
                }
                if (i > 0) {
                    for (int j = 0, len = EHundredArenaRebType.values().length; j < len; ++j) {
                        rebInfo.allReb.put(j, temp.getAllRebValueByType(EHundredArenaRebType.values()[j]) / 100);
                        rebInfo.myReb.put(j, temp.getRebValue(EHundredArenaRebType.values()[j], roomPlayer.getUid()) / 100);
                    }
                }
                info.allReb.add(rebInfo);
            }
        }
        roomPlayer.send(CommandId.CLI_NTF_ARENA_HUNDRED_JOIN_OK, info);
    }

    /**
     * 玩家是否在上庄列表中(当前庄不在上庄列表中)
     * 
     * @param playerUid
     * @return
     */
    public boolean isInBankerList(long playerUid) {
        Iterator<IHundredBanker> it = this.bankerList.iterator();
        while (it.hasNext()) {
            IHundredBanker hundredBanker = it.next();
            if (hundredBanker == null) {
                continue;
            }
            if (hundredBanker.getUid() == playerUid) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ErrorCode leave(Player player) {
        IHundredPlayer hundredPlayer = this.allPlayer.get(player.getUid());
        if (null == hundredPlayer) {
            Logs.ARENA.warn("%s %s 不在竞技场里, 无法离开竞技场", this.room, player);
            return ErrorCode.PLAYER_ARENA_NOT_IN;
        }
        // 是否已经下注
        if (this.allRebUid.contains(player.getUid())) {
            Logs.ARENA.warn("%s 已经下注, 无法离开", player);
            // ((IRoomPlayer) hundredPlayer).changeState(EState.LEAVE);
            // this.offlinePlayerUid.add(player.getUid());
            return ErrorCode.ARENA_HUNDRED_ALREADY_REB;
        }
        // 是否是正在上庄
        if ((this.curBanker != null && this.curBanker.getUid() == player.getUid()) || isInBankerList(player.getUid())) {
            Logs.ARENA.warn("%s 当前庄家, 无法离开", player);
            // ((IRoomPlayer) hundredPlayer).changeState(EState.LEAVE);
            // this.offlinePlayerUid.add(player.getUid());
            return ErrorCode.ARENA_HUNDRED_CURRENT_BANKER;
        }
        // vip座位站起
        vipSeatSitup(hundredPlayer);
        this.allPlayer.remove(player.getUid());
        this.offlinePlayerUid.remove(player.getUid());
        // 设置当前房间为null
        ((IRoomPlayer)hundredPlayer).setRoom(null);
        return ErrorCode.OK;
    }

    /**
     * vip座位站起
     * 
     * @param hundredPlayer
     */
    protected void vipSeatSitup(IHundredPlayer hundredPlayer) {
        if (null != hundredPlayer && -1 != hundredPlayer.getVipSeatIndex()) {
            int index = hundredPlayer.getVipSeatIndex();
            this.vipSeat[index] = 0;
            hundredPlayer.setVipSeatIndex(-1);
            hundredPlayer.setNoRebRound(0);
            // 广播给游戏桌中所有玩家，vip座位变更
            this.broadcast(CommandId.CLI_NTF_ARENA_HUNDRED_VIP_SEAT_INFO,
                new PCLIHundredVipSeatInfo(-1, null, null, -1, index));
        }
    }

    @Override
    public void broadcast(int commandId, Object msg) {
        Iterator<Map.Entry<Long, IHundredPlayer>> it = this.allPlayer.entrySet().iterator();
        while (it.hasNext()) {
            IRoomPlayer player = (IRoomPlayer)it.next().getValue();
            if (player.isOffline()) {
                continue;
            }
            player.send(commandId, msg);
        }
    }

    @Override
    public ErrorCode offline(Player player) {
        Logs.ARENA.warn("%s %s 下线, 离开", this.room, player);
        ErrorCode errorCode = leave(player);
        if (errorCode == ErrorCode.OK) {
            // 直接离开房间处理
            player.changeRoomId(-1, -1);
        }
        return errorCode;
    }

    @Override
    public void destoryAfter() {
        super.destoryAfter();
        this.offlinePlayerUid.clear();
        this.allPlayer.clear();
    }

    @Override
    public void tickHandle(long curTime, long delay) {
        if (null == this.box) {
            return;
        }
        if (this.box.isClose()) {
            return;
        }
        this.noticeRebTotal(curTime);
        if (!(-1 == this.expire || this.expire < curTime)) {
            return;
        }

        if (this.isInit() && (this.allPlayer.size() <= this.offlinePlayerUid.size())) {
            return;
        }

        this.expire = -1;
        if (this.gameState.compareAndSet(EHundredGameState.INIT, EHundredGameState.READY_BEGIN)) {
            beginyHandle(curTime);
        } else if (this.gameState.compareAndSet(EHundredGameState.READY_BEGIN, EHundredGameState.BEGIN_REB)) {
            beginRebHandle(curTime);
        } else if (this.gameState.compareAndSet(EHundredGameState.BEGIN_REB, EHundredGameState.OPEN_CARD)) {
            openCardHandle(curTime);
        } else if (this.gameState.compareAndSet(EHundredGameState.OPEN_CARD, EHundredGameState.OVER)) {
            overHandle(curTime);
        } else if (this.gameState.compareAndSet(EHundredGameState.OVER, EHundredGameState.INIT)) {
            initHandle(curTime);
        }
    }

    /**
     * 通知每个下注区域的总下注值
     */
    protected void noticeRebTotal(long curTime) {
        if (this.gameState.get() != EHundredGameState.BEGIN_REB) {
            return;
        }
        if (!needNoticeAllRecord) {
            return;
        }
        if (curTime < nextNoticeTime) {
            return;
        }
        nextNoticeTime = curTime + 1000;
        needNoticeAllRecord = false;
        //通知所有人
        PCLIHundredNtfTouzhurenRebInfoByLhd rebInfoByLhd = new PCLIHundredNtfTouzhurenRebInfoByLhd();
        rebInfoByLhd.boixId = this.getBoxUid();
        for (int i = 0; i < rebAllRecords.length; i++) {
            rebInfoByLhd.rebInfo.add(rebAllRecords[i] / 100);
        }
        rebInfoByLhd.remainRebTotal = this.remainRebValue;
        rebInfoByLhd.remainReb = this.leftAreanVlaue;

        IHundredHandle hundredHandle = this;
        AsyncTask.I.addTask(new Runnable() {
            @Override
            public void run() {
                hundredHandle.broadcast(CommandId.CLI_NTF_ARENA_HUNDRED_REB_ALL_INFO, rebInfoByLhd);
            }
        });
    }

    /**
     * 初始处理
     * 
     * @param curTime
     */
    private void initHandle(long curTime) {
        this.doClear();
        this.expire = curTime + 1;
    }

    protected void doClear() {
        for (int i = 0; i < this.playerCnt; ++i) {
            this.allReb[i].clear();
        }
        // 遍历下注过的玩家，如果不在线，就从allPlayer中删除
        Iterator<Long> it = this.allRebUid.iterator();
        while (it.hasNext()) {
            IHundredPlayer player = this.allPlayer.get(it.next());
            if (null == player) {
                continue;
            }
            IRoomPlayer roomPlayer = (IRoomPlayer)player;
            roomPlayer.clear();
            if (roomPlayer.isOffline()) {
                this.vipSeatSitup(player);
                this.offlinePlayerUid.remove(roomPlayer.getUid());
                this.allPlayer.remove(roomPlayer.getUid());
            }
        }
        this.allRebUid.clear();
        // 遍历剩余allPlayer，如果不在线，又没上庄就从allPlayer中删除
        for (long key : this.allPlayer.keySet()) {
            IHundredPlayer player = this.allPlayer.get(key);
            if (null == player) {
                continue;
            }
            IRoomPlayer roomPlayer = (IRoomPlayer)player;
            if (roomPlayer.isOffline() && !isInBankerList(player.getUid())
                && (this.curBanker == null || this.curBanker.getUid() != player.getUid())) {
                roomPlayer.clear();
                this.vipSeatSitup(player);
                this.offlinePlayerUid.remove(roomPlayer.getUid());
                this.allPlayer.remove(roomPlayer.getUid());
            }
        }

        if (this.awayBureauClearCard) {
            this.allCard.clear();
        }
        if (null != this.curBanker) {
            this.curBanker.clear();
        }
        this.bigWinPlayerUid = -1;
        this.allRebValue = 0;
    }

    /**
     * 结束处理
     * 
     * @param curTime
     */
    private void overHandle(long curTime) {
        this.curBureauEndTime = curTime;
        this.doOver();
        if (this.curBanker.isDown() || this.checkDownBanker(this.curBanker)) {
            if (!this.curBanker.isSystem()) {
                IHundredPlayer bankerPlayer = this.allPlayer.get(this.curBanker.getUid());
                bankerPlayer.decBankerCnt();
            }
            this.doDownBanker(this.curBanker);
            this.curBanker = null;
        }
        this.expire = curTime + this.overTime;
    }

    protected void doDownBanker(IHundredBanker banker) {
        // 修改庄家竞技值
        IBoxOwner boxOwner = getRoom().getBoxOwner();
        if (null != boxOwner) {
            IClub mainClub = (IClub)boxOwner;
            boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(banker.getUid()), banker.getUid(),
                banker.getValue(), 0, false);
            banker.addValue(-banker.getValue());
        }
    }

    protected abstract boolean checkBanker(IHundredBanker bankerPlayer);

    protected boolean checkDownBanker(IHundredBanker bankerPlayer) {
        if (bankerPlayer.getBureau() >= this.maxBureau) {
            return true;
        }
        if (bankerPlayer.getValue() < this.downBankerValue) {
            return true;
        }
        if (!this.checkEnoughGold(bankerPlayer.getUid())) {
            return true;
        }
        return false;
    }

    protected boolean checkEnoughGold(long playerUid) {
        IClub club = (IClub)room.getBoxOwner();
        long fromClubUid = club.getEnterFromClubUid(playerUid);
        IClub fromClub = ClubManager.I.getClubByUid(fromClubUid);
        if (fromClub != null) {
            return BoxManager.I.checkEnoughGold(fromClub, room.getRule(), playerUid, false);
        }
        return false;
    }

    /**
     * 检查玩家竞技值是否足够
     * 
     * @param playerUid
     * @param value
     *            实际值的100倍
     * @return
     */
    protected boolean checkEnoughGold(long playerUid, long value) {
        IClub club = ClubManager.I.getClubByUid(this.getRoom().getGroupUid());
        if (club != null) {
            long tempClubUid = club.getEnterFromClubUid(playerUid);
            if (tempClubUid != club.getClubUid()) {
                IClub tempClub = ClubManager.I.getClubByUid(tempClubUid);
                if (tempClub != null) {
                    club = tempClub;
                }
            }
            return club.getGold(playerUid) >= value;
        }
        return false;
    }

    /**
     * 获取百人场下注记录
     * 
     * @return
     */
    private List<HundredRebRecordInfo> getHundredRebRecords() {
        List<HundredRebRecordInfo> rebList = new ArrayList<>();
        Iterator<Long> it = this.allRebUid.iterator();
        HundredPlayerInfo bankerPlayer = this.allReb[0];
        while (it.hasNext()) {
            long playerUid = it.next();
            HundredRebRecordInfo rebRecordInfo = new HundredRebRecordInfo();
            rebRecordInfo.setUid(UIDManager.I.getAndInc(UIDType.HUNDRED_REB_RECORD));
            rebRecordInfo.setRoomUid(this.room.getRoomUid());
            rebRecordInfo.setRoomId(this.room.getRoomId());
            rebRecordInfo.setClubUid(this.room.getGroupUid());
            rebRecordInfo.setRebPlayerUid(playerUid);
            rebRecordInfo.setTime(this.curBureauBeginTime);
            rebRecordInfo.setBankerCardType(bankerPlayer.getCardType().getValue());
            // 游戏类型
            rebRecordInfo.setGameType(this.room.getGameType());

            // 获取返利值
            IHundredPlayer m_arenaPlayer = this.allPlayer.get(playerUid);
            rebRecordInfo.setFanliValue(m_arenaPlayer.getVipSeatFanliValue());
            m_arenaPlayer.setVipSeatFanliValue(0);

            for (int i = 1; i < this.playerCnt; ++i) {
                HundredRebRecordInfo.AllRebInfo allRebInfo = new HundredRebRecordInfo.AllRebInfo();
                HundredPlayerInfo player = this.allReb[i];
                for (int j = 0, len = EHundredArenaRebType.values().length; j < len; ++j) {
                    EHundredArenaRebType type = EHundredArenaRebType.values()[j];
                    int rebValue = player.getRebValue(type, playerUid);
                    if (rebValue < 1) {
                        continue;
                    }
                    HundredRebRecordInfo.RebInfo rebInfo = new HundredRebRecordInfo.RebInfo();
                    rebInfo.setRebValue(rebValue);
                    rebInfo.setWinValue(player.getResultValue(type, playerUid));

                    allRebInfo.addRebInfo(type, rebInfo);
                    rebRecordInfo.setRebValue(rebRecordInfo.getRebValue() + rebInfo.getRebValue());
                    rebRecordInfo.setWinValue(rebRecordInfo.getWinValue() + rebInfo.getWinValue());
                }
                allRebInfo.setCardType(player.getCardType().getValue());
                allRebInfo.getCards().addAll(getCards(player));
                rebRecordInfo.getRebInfo().add(allRebInfo);
            }
            rebList.add(rebRecordInfo);
        }
        return rebList;
    }

    /**
     * 获取百人场局数记录
     * 
     * @return
     */
    private HundredBureauRecordInfo getHundredBureauRecordInfo() {
        HundredBureauRecordInfo bureauRecordInfo = new HundredBureauRecordInfo();
        bureauRecordInfo.setUid(UIDManager.I.getAndInc(UIDType.HUNDRED_BUREAU_RECORD));
        bureauRecordInfo.setRoomUid(this.room.getRoomUid());
        bureauRecordInfo.setRoomId(this.room.getRoomId());
        bureauRecordInfo.setTime(this.curBureauBeginTime);
        bureauRecordInfo.setEndTime(this.curBureauEndTime);
        bureauRecordInfo.setBankerPlayerUid(this.curBanker.getUid());
        bureauRecordInfo.setBankerWinValue(this.curBanker.getWinScore());
        bureauRecordInfo.setDirty(true);
        for (int i = 0; i < this.playerCnt; ++i) {
            HundredPlayerInfo player = this.allReb[i];
            HundredBureauRecordInfo.CardInfo cardInfo = new HundredBureauRecordInfo.CardInfo();
            cardInfo.setCardType(player.getCardType().getValue());
            cardInfo.getCards().addAll(getCards(player));
            cardInfo.setWin(player.isWin());
            for (EHundredArenaRebType type : EHundredArenaRebType.values()) {
                cardInfo.getRebs().put(type.ordinal(), player.getAllRebValueByType(type));
            }
            cardInfo.setValue(-player.getAllResultValue(EHundredArenaRebType.PLAYER_WIN));
            bureauRecordInfo.getCardInfo().add(cardInfo);
        }
        return bureauRecordInfo;
    }

    /**
     * 结束时记录处理
     */
    protected void doRecord() {
        final List<HundredRebRecordInfo> rebList = getHundredRebRecords();
        // 本地内存记录百人场下注数据
        recordRebRecordInfo(rebList);
        final HundredBureauRecordInfo bureauRecordInfo = getHundredBureauRecordInfo();
        // 本地内存记录百人场对局数据
        bureauRecordInfoList.add(0, bureauRecordInfo);
        if (bureauRecordInfoList.size() > MAX_BUREAU_CACHE_SIZE) {
            bureauRecordInfoList.remove(bureauRecordInfoList.get(bureauRecordInfoList.size() - 1));
        }
        DBManager.I.save(new Task() {
            @Override
            public void run() {
                DBManager.I.getHundredRebRecordDao().saveAll(rebList);
                DBManager.I.update(bureauRecordInfo);
            }
        });
    }

    /**
     * 记录百人场下注数据
     * 
     * @param rebList
     */
    private void recordRebRecordInfo(List<HundredRebRecordInfo> rebList) {
        if (null == rebList) {
            return;
        }
        try {
            for (HundredRebRecordInfo temp : rebList) {
                recordRebRecordInfo(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 记录百人场战绩进内存中
     * 
     * @param data
     */
    private void recordRebRecordInfo(HundredRebRecordInfo data) {
        LinkedHashMap<Long, HundredRebRecordInfo> dataMap = null;
        if (rebRecordInfoMap.containsKey(data.getRebPlayerUid())) {
            dataMap = rebRecordInfoMap.get(data.getRebPlayerUid());
        } else {
            dataMap = new LinkedHashMap<Long, HundredRebRecordInfo>(INITIAL_CAPACITY) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Long, HundredRebRecordInfo> eldest) {
                    return size() > MAX_CACHE_SIZE;
                }
            };
        }
        dataMap.put(data.getUid(), data);
        rebRecordInfoMap.put(data.getRebPlayerUid(), dataMap);
    }

    @Override
    public List<HundredRebRecordInfo> getRebRecordInfos(long playerUid) {
        if (rebRecordInfoMap.containsKey(playerUid)) {
            LinkedHashMap<Long, HundredRebRecordInfo> logMap = rebRecordInfoMap.get(playerUid);
            int size = logMap.size();
            if (size > 0) {
                List<HundredRebRecordInfo> list = new ArrayList(logMap.values());
                if (size > 1) {
                    Collections.sort(list, new Comparator<HundredRebRecordInfo>() {
                        @Override
                        public int compare(HundredRebRecordInfo ele1, HundredRebRecordInfo ele2) {
                            return (int)(ele2.getTime() - ele1.getTime());
                        }
                    });
                }
                return list;
            }
        }
        return null;
    }

    /**
     * 获取百人场对局记录
     * 
     * @return
     */
    @Override
    public List<HundredBureauRecordInfo> getRBureauRecordInfos() {
        return bureauRecordInfoList;
    }

    @Override
    public void record() {

    }

    protected abstract List<Byte> getCards(HundredPlayerInfo playerInfo);

    /**
     * 执行结束
     */
    protected void doOver() {
        int oldBankerValue = this.curBanker.getValue();
        // 结束处理
        this.doOverHandle();
        // 记录战绩相关数据
        this.doRecord();
        this.doOverAfter(oldBankerValue);
    }

    private void doOverAfter(int oldBankerValue) {
        int bankerValue = (this.curBanker.getValue() - oldBankerValue) / 100;
        Iterator<Map.Entry<Long, IHundredPlayer>> it = this.allPlayer.entrySet().iterator();
        while (it.hasNext()) {
            IHundredPlayer player = (IHundredPlayer)it.next().getValue();
            if (player.isOffline()) {
                continue;
            }
            PCLIHundredNtfOverByLhd info = new PCLIHundredNtfOverByLhd();
            info.boxId = this.getBoxUid();
            info.roomId = this.getRoomId();
            info.bankerValue = bankerValue;
            info.myValue = player.getScore(Score.SCORE, false) / 100;

            this.fillVipSeatArenaInfo(info);
            player.send(CommandId.CLI_NTF_ARENA_HUNDRED_OVER, info);
        }

        // 结算的时候判断座位玩家连续没下注次数
        for (int i = 0; i < this.vipSeat.length; i++) {
            IHundredPlayer arenaPlayer = (IHundredPlayer)this.allPlayer.get(this.vipSeat[i]);
            if (arenaPlayer == null) {
                continue;
            }
            int m_autoupCount = this.room.getRule().getOrDefault(RoomRule.RR_HUNDRED_VIP_SEAT_AUTOUP, 0);
            // 如果超过,就下坐
            if (arenaPlayer.getNoRebRound() >= m_autoupCount / 100 && m_autoupCount > 0) {
                this.vipSeatSitup(arenaPlayer);
            }
        }
        // 推送记录
        this.sendRecord();
    }

    /**
     * 推送记录
     */
    protected void sendRecord() {
        PCLIHundredNtfRecord record = new PCLIHundredNtfRecord();
        record.boxId = this.getBoxUid();
        record.roomId = this.getRoomId();
        record.reb = false;// false:房间记录
        record.page = 0;
        record.pageSize = MAX_BUREAU_CACHE_SIZE;
        List<HundredBureauRecordInfo> list = this.getRBureauRecordInfos();
        for (HundredBureauRecordInfo recordInfo : list) {
            PCLIHundredNtfRecord.BankerRecord bankerRecord = new PCLIHundredNtfRecord.BankerRecord();
            bankerRecord.time = recordInfo.getTime();
            for (int i = 0; i < recordInfo.getCardInfo().size(); ++i) {
                HundredBureauRecordInfo.CardInfo temp = recordInfo.getCardInfo().get(i);
                PCLIHundredNtfRecord.CardInfo cardInfo = new PCLIHundredNtfRecord.CardInfo();
                cardInfo.cardType = temp.getCardType();
                cardInfo.cards.addAll(temp.getCards());
                cardInfo.win = temp.isWin();
                cardInfo.cards = temp.getCards();
                bankerRecord.cardInfo.add(cardInfo);
            }
            record.bankerList.add(bankerRecord);
        }
        record.next = false;

        this.broadcast(CommandId.CLI_NTF_ARENA_HUNDRED_RECORD_OK, record);
    }

    protected void fillVipSeatArenaInfo(PCLIHundredNtfOverByLhd info) {
        for (int i = 0, len = this.vipSeat.length; i < len; ++i) {
            if (0 != this.vipSeat[i]) {
                IHundredPlayer hundredPlayer = (IHundredPlayer)this.allPlayer.get(this.vipSeat[i]);
                // 返利
                int fanliValue = 0;
                if (hundredPlayer != null) {
                    info.vipSeatFanliValue.put(this.vipSeat[i], hundredPlayer.getVipSeatFanliValue());
                    fanliValue = hundredPlayer.getVipSeatFanliValue();
                }
                if (null != hundredPlayer) {
                    info.vipSeatArenaValue.put(this.vipSeat[i], (int)hundredPlayer.getGold(this.getRoom()));
                    info.vipSeatWinOrLostArenaValue.put(this.vipSeat[i],
                        hundredPlayer.getScore(Score.SCORE, false) - fanliValue);
                }
            }
        }
        info.bigWInPlayerUid = this.bigWinPlayerUid;
    }

    /**
     * 结束处理
     */
    private void doOverHandle() {
        if (this.allRebUid.size() <= 0) {
            return;
        }
        int bankerAddValue = 0;
        int serviceValue = 0;

        int maxWinValue = Integer.MIN_VALUE;
        long now = System.currentTimeMillis();
        Iterator<Long> it = this.allRebUid.iterator();
        IBoxOwner boxOwner = getRoom().getBoxOwner();
        IClub mainClub = (IClub)boxOwner;
        while (it.hasNext()) {
            long playerUid = it.next();
            int winValue = 0;
            int allRebValue = 0;
            IHundredPlayer m_player = this.allPlayer.get(playerUid);
            IRoomPlayer roomPlayer = (IRoomPlayer)m_player;
            int fanliValue = m_player.getVipSeatFanliValue();// 返利值
            for (int i = 1; i < this.playerCnt; ++i) {
                HundredPlayerInfo player = this.allReb[i];
                for (int j = 0, len = EHundredArenaRebType.values().length; j < len; ++j) {
                    EHundredArenaRebType type = EHundredArenaRebType.values()[j];
                    int rebValue = player.getRebValue(type, playerUid);
                    if (rebValue < 1) {
                        continue;
                    }
                    boolean win = player.isWin(type);
                    int value = this.getWinOrLostValue(type, this.allReb[0], player, win, rebValue);
                    winValue += value;
                    allRebValue += rebValue;
                    player.setWinOrLostValue(type, playerUid, value);
                }
            }
            if (winValue > maxWinValue) {
                maxWinValue = winValue;
                if (maxWinValue > 0) {
                    this.bigWinPlayerUid = playerUid;
                }
            }
            bankerAddValue -= winValue;
            roomPlayer.addScore(Score.SCORE, winValue + fanliValue, false);
            // 计算玩家实际竞技分
            boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(roomPlayer.getUid()), roomPlayer.getUid(),
                winValue + allRebValue + fanliValue, 0, false);

            // 抽水处理
            if (winValue > 0) {
                serviceValue = (int)(winValue * (costModelValue / 100f));
                if (serviceValue > 0) {
                    // 扣水，返回实际抽水值
                    serviceValue = boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(playerUid), playerUid,
                        -serviceValue, 0, false);
                    boxOwner.divideServiceCharge(box.getUid(), playerUid, serviceValue * 100, now);
                }
            }

        }

        this.curBanker.addValue(bankerAddValue);
        this.curBanker.setWinScore(bankerAddValue);
        // 庄家抽水处理
        serviceValue = 0;
        if (bankerAddValue > 0) {
            serviceValue = (int)(bankerAddValue * (costModelValue / 100f));
            if (serviceValue > 0) {
                // 扣水，返回实际抽水值
                serviceValue = bankerAddValue > serviceValue ? serviceValue : bankerAddValue;
                this.curBanker.addValue(-serviceValue);
                this.curBanker.setWinScore(bankerAddValue - serviceValue);
                // serviceValue = boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(this.curBanker.getUid()),
                // this.curBanker.getUid(), -serviceValue, 0);
                boxOwner.divideServiceCharge(box.getUid(), this.curBanker.getUid(), serviceValue * 100, now);
            }
        }

        // this.curBanker.addValue(bankerAddValue - serviceValue);
        // 更新玩家任务
        long bankerplayerId = curBanker.getUid();
        Set<Long> playerIds = new HashSet<>();
        playerIds.add(bankerplayerId);
        for (Long v : allRebUid) {
            playerIds.add(v);
        }
        ClubManager.I.getMainClub(boxOwner).onFinishGame(getBoxUid(), playerIds);

        for (int i = 0, len = this.vipSeat.length; i < len; ++i) {
            if (0 != this.vipSeat[i]) {
                IRoomPlayer arenaPlayer = (IRoomPlayer)this.allPlayer.get(this.vipSeat[i]);
                if (null != arenaPlayer) {
                    // 检测竞技场分低于值就下座
                    if (!checkEnoughGold(arenaPlayer.getUid(), this.vipSeatLimit)) {
                        // 站起
                        this.vipSeatSitup((IHundredPlayer)arenaPlayer);
                    }
                }
            }
        }
    }

    protected abstract int getWinOrLostValue(EHundredArenaRebType type, HundredPlayerInfo banker,
        HundredPlayerInfo player, boolean isWin, int value);

    /**
     * 开牌处理
     * 
     * @param curTime
     */
    private void openCardHandle(long curTime) {
        // 开牌扣房卡
        IClub mainClub = (IClub)getRoom().getBoxOwner();
        if (!mainClub.getOwnerPlayer().addMoney(EMoneyType.DIAMOND, -this.roomCard(), mainClub.getOwnerId(),
            mainClub.getClubUid(), EMoneyExpendType.GROUP_EXPEND, -1)) {
            Logs.ROOM.warn("%d openCardHandle,房卡不足", mainClub.getClubUid());
        }
        this.doOpenCard();
        this.expire = curTime + getOpenCardTime();
    }

    /**
     * 执行开牌
     */
    protected abstract void doOpenCard();

    public int getOpenCardTime() {
        return openCardTime;
    }

    /**
     * 开始下注
     * 
     * @param curTime
     */
    private void beginRebHandle(long curTime) {
        this.doBeginReb();
        this.expire = curTime + getRebTime();
    }

    public int getRebTime() {
        return rebTime;
    }

    /**
     * 执行开始下注
     */
    protected void doBeginReb() {
        PCLIHundredNtfReb info = new PCLIHundredNtfReb();
        info.boxUid = this.getBoxUid();
        this.broadcast(CommandId.CLI_NTF_ARENA_HUNDRED_REB, info);
        // 开始下注的时候，每个座位玩家没下注的轮数+1
        for (int i = 0; i < this.vipSeat.length; i++) {
            IHundredPlayer arenaPlayer = (IHundredPlayer)this.allPlayer.get(this.vipSeat[i]);
            if (arenaPlayer != null) {
                arenaPlayer.setNoRebRound(arenaPlayer.getNoRebRound() + 1);
            }
        }
    }

    protected ErrorCode checkReb(IPlayer player, int index, int value, EHundredArenaRebType type) {
        if (index < 1 || index > this.playerCnt) {
            Logs.ARENA.warn("%s %s 不能押注, 不能押庄, index:%d", this, player, index);
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        if (EHundredArenaRebType.PLAYER_WIN != type) {
            Logs.ARENA.warn("%s %s 不能押注, 无效押注类型, type:%s", this, player, type);
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        // 玩家竞技值不足
        if (!this.checkEnoughGold(player.getUid(), value)) {
            return ErrorCode.ARENA_LESS_THAN_MIN_VALUE;
        }
        int needArenaValue = this.needBankerArenaValue(index, value, type);
        int temp = this.remainRebValue - needArenaValue;
        // 庄家剩余可下注金额不足
        if (temp < 0) {
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        this.leftAreanVlaue = temp;
        if (this.leftAreanVlaue <= 0) {
            this.expire = System.currentTimeMillis();
        }
        return ErrorCode.OK;
    }

    protected abstract int needBankerArenaValue(int index, int value, EHundredArenaRebType type);

    /**
     * 通知下注
     * 
     * @param player
     * @param index
     * @param type
     * @param value
     * @param onlySelf
     *            是否只通知自己
     */
    protected void notifyReb(IPlayer player, int index, EHundredArenaRebType type, int value, boolean onlySelf) {
        PCLIHundredNtfRebInfoByLhd info = new PCLIHundredNtfRebInfoByLhd();
        info.boixId = this.getBoxUid();
        info.playerUid = player.getUid();
        info.index = index;
        info.value = value / 100;
        info.type = type.ordinal();
        info.remainRebTotal = this.remainRebValue;
        info.remainReb = this.leftAreanVlaue;

        IHundredHandle hundredHandle = this;
        AsyncTask.I.addTask(new Runnable() {
            @Override
            public void run() {
                if (onlySelf) {
                    player.send(CommandId.CLI_NTF_ARENA_HUNDRED_REB_INFO, info);
                } else {
                    hundredHandle.broadcast(CommandId.CLI_NTF_ARENA_HUNDRED_REB_INFO, info);
                }
            }
        });

        // 计算返利
        int m_fanlilv = this.room.getRule().getOrDefault(RoomRule.RR_HUNDRED_VIP_SEAT_FANLI, 0) / 100;
        if (m_fanlilv > 0) {
            for (int i = 0; i < this.vipSeat.length; i++) {
                if (this.vipSeat[i] == player.getUid()) {
                    HundredLhdPlayer hundredLhdPlayer = (HundredLhdPlayer)this.allPlayer.get(player.getUid());
                    int m_fanli = value / 100 * m_fanlilv;
                    hundredLhdPlayer.setVipSeatFanliValue(hundredLhdPlayer.getVipSeatFanliValue() + m_fanli);
                    return;
                }
            }
        }
    }

    /**
     * 开始处理
     * 
     * @param curTime
     */
    private void beginyHandle(long curTime) {
        this.curBanker = this.generateBanker();
        if (null == this.curBanker) {
            this.gameState.set(EHundredGameState.INIT);
            return;
        }
        // 房卡是否足够
        IClub mainClub = (IClub)getRoom().getBoxOwner();
        if (!mainClub.hasEnoughMoney(EMoneyType.DIAMOND, this.roomCard())) {
            this.bankerList.add(0, this.curBanker);
            this.gameState.set(EHundredGameState.INIT);
            this.curBanker = null;
            return;
        }

        this.curBureauBeginTime = curTime;
        this.curBanker.incBureau();

        this.doShuffle();
        this.doReadyBegin();
        this.expire = curTime + getReadyBeginTime();
    }

    public int getReadyBeginTime() {
        return readyBeginTime;
    }

    /**
     * 洗牌
     */
    protected abstract void doShuffle();

    /**
     * 准备开始处理
     */
    protected abstract void doReadyBegin();

    protected IHundredBanker generateBanker() {
        IHundredBanker bankerPlayer = this.curBanker;
        if (null == bankerPlayer && !this.bankerList.isEmpty()) {
            bankerPlayer = this.bankerList.remove(0);
        }
        return bankerPlayer;
    }

    protected boolean isStart() {
        return this.gameState.get().ordinal() >= EHundredGameState.READY_BEGIN.ordinal()
            && this.gameState.get().ordinal() < EHundredGameState.OVER.ordinal();
    }

    protected boolean isOpenCard() {
        return EHundredGameState.OPEN_CARD == this.gameState.get();
    }

    protected boolean isOver() {
        return EHundredGameState.OVER == this.gameState.get();
    }

    protected boolean isInit() {
        return EHundredGameState.INIT == this.gameState.get();
    }

    protected boolean isReb() {
        return EHundredGameState.BEGIN_REB == this.gameState.get();
    }

    @Override
    public ErrorCode vipSeatOp(long playerUid, int index) {
        if (-1 == index) {
            // 站起
            if (playerUid == this.vipSeat[index]) {
                this.vipSeat[index] = 0;
            }
            IHundredPlayer hundredPlayer = (IHundredPlayer)this.allPlayer.get(playerUid);
            if (null != hundredPlayer) {
                hundredPlayer.setVipSeatIndex(-1);
                hundredPlayer.setNoRebRound(0);
            }
            this.broadcast(CommandId.CLI_NTF_ARENA_HUNDRED_VIP_SEAT_INFO,
                new PCLIHundredVipSeatInfo(-1, null, null, -1, index));
            return ErrorCode.OK;
        } else {
            if (index < 0 || index >= this.vipSeat.length) {
                Logs.ARENA.warn("%s 无效索引:%d 坐下失败", this, index);
                return ErrorCode.REQUEST_INVALID_DATA;
            }
            if (0 != this.vipSeat[index]) {
                Logs.ARENA.warn("%s 索引:%d 已经有人了 坐下失败", this, index);
                return ErrorCode.ARENA_HUNDRED_VIP_SEAT_HAS;
            }
            IHundredPlayer hundredPlayer = (IHundredPlayer)this.allPlayer.get(playerUid);
            if (null == hundredPlayer) {
                Logs.ARENA.warn("%s 玩家:%d 没在百人场中 已经有人了 坐下失败", this, playerUid);
                return ErrorCode.ARENA_HUNDRED_NOT_IN;
            }
            if (!this.checkEnoughGold(playerUid, this.vipSeatLimit)) {
                Logs.ARENA.warn("%s 玩家:%d 竞技值不足无法坐下 已经有人了 坐下失败", this, playerUid);
                return ErrorCode.ARENA_HUNDRED_VIP_SEAT_LIMIT;
            }
            if (-1 != hundredPlayer.getVipSeatIndex()) {
                this.vipSeat[hundredPlayer.getVipSeatIndex()] = 0;
                this.broadcast(CommandId.CLI_NTF_ARENA_HUNDRED_VIP_SEAT_INFO,
                    new PCLIHundredVipSeatInfo(-1, null, null, -1, hundredPlayer.getVipSeatIndex()));
            }
            this.vipSeat[index] = playerUid;
            hundredPlayer.setVipSeatIndex(index);
            IPlayer player = PlayerManager.I.getOnlinePlayer(playerUid);
            if (null != player) {
                this.broadcast(CommandId.CLI_NTF_ARENA_HUNDRED_VIP_SEAT_INFO, new PCLIHundredVipSeatInfo(
                    player.getUid(), player.getName(), player.getIcon(), hundredPlayer.getGold(this.getRoom()), index));
            }
        }
        return ErrorCode.OK;
    }

    /**
     * 填充VIP座位信息
     * 
     * @param info
     *            桌面信息
     */
    protected void fillVipSeatInfo(PCLIHundredNtfDeskInfo info) {
        IClub rootClub = ClubManager.I.getClubByUid(this.getRoom().getGroupUid());
        for (int i = 0, len = this.vipSeat.length; i < len; ++i) {
            if (0 == this.vipSeat[i]) {
                continue;
            }
            IHundredPlayer hundredPlayer = this.allPlayer.get(this.vipSeat[i]);
            if (null == hundredPlayer) {
                continue;
            }
            IPlayer player = PlayerManager.I.getPlayer(hundredPlayer.getUid());
            if (null != player) {
                long tempClubUid = rootClub.getEnterFromClubUid(player.getUid());
                IClub club = rootClub;
                if (rootClub.getClubUid() != tempClubUid) {
                    club = ClubManager.I.getClubByUid(tempClubUid);
                }
                info.vipSeatInfoList.add(new PCLIHundredVipSeatInfo(player.getUid(), player.getName(), player.getIcon(),
                    club.getGold(player.getUid()), i));
            }
        }
    }

    @Override
    public IHundredPlayer[] getAllPlayerList() {
        return this.allPlayer.values().toArray(new IHundredPlayer[0]);
    }

    @Override
    public CopyOnWriteArrayList<IHundredBanker> getBankerList() {
        return bankerList;
    }

    @Override
    public IHundredBanker getCurBanker() {
        return curBanker;
    }

    @Override
    public IRoomPlayer getRoomPlayer(long playerUid) {
        IHundredPlayer hundredPlayer = this.allPlayer.get(playerUid);
        return (IRoomPlayer)hundredPlayer;
    }

    @Override
    public void doDestroy() {
        saveAll();
    }

    public long[] getVipSeats() {
        return this.vipSeat;
    }

    private void saveAll() {
        if (!this.isInit()) {
            if (this.curBanker != null) {
                this.curBanker.down(true);
            }
            this.overHandle(System.currentTimeMillis());
        }
        Iterator<IHundredBanker> it = this.bankerList.iterator();
        IClub mainClub = (IClub)getRoom().getBoxOwner();
        while (it.hasNext()) {
            IHundredBanker banker = it.next();
            // 庄家返钱
            mainClub.addMemberValueByBox(mainClub.getEnterFromClubUid(banker.getUid()), banker.getUid(),
                banker.getValue(), 0, false);
        }
    }

    /**
     * 房卡消耗
     * 
     * @return
     */
    public Float roomCard() {
        return 1f;
    }

    @Override
    public ErrorCode upBanker(IPlayer player, HashMap<String, Integer> param) {
        IHundredPlayer hundredPlayer = (IHundredPlayer)this.allPlayer.get(player.getUid());
        if (null == hundredPlayer) {
            Logs.ARENA.warn("%s %s 不在百人场中无法上庄", this, player);
            return ErrorCode.PLAYER_ARENA_NOT_IN;
        }
        if (this.downBankerValue >= this.upBankerValue) {
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        // 客户端请求的上庄分值
        int m_upBankerValue = param.getOrDefault(HundredLhdBanker.KEY_VALUE, 0) * 100;
        if (m_upBankerValue < this.upBankerValue) {
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        // 判断身上竞技值是否足够
        if (!this.checkEnoughGold(player.getUid(), m_upBankerValue)) {
            return ErrorCode.ARENA_LESS_THAN_MIN_VALUE;
        }

        IBoxOwner boxOwner = getRoom().getBoxOwner();
        if (null == boxOwner) {
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        IClub mainClub = (IClub)boxOwner;
        int finalScore = boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(player.getUid()), player.getUid(),
            -m_upBankerValue, 0, false);
        if (m_upBankerValue > finalScore) {
            Logs.ARENA.error("%s %s 百人场上庄失败,扣除玩家竞技分 %d", this, player, finalScore);
            return ErrorCode.ARENA_LESS_THAN_MIN_VALUE;
        }

        IHundredBanker bankerPlayer = new HundredLhdBanker(this.bankerUid++, player, param);
        hundredPlayer.incBankerCnt();
        if (this.bankerList.size() == 0) {
            this.bankerList.add(bankerPlayer);
        } else if (this.upBankerOrder == 2) {// 金额大小上庄
            // Collections.sort(this.bankerList, new Comparator<IHundredBanker>() {
            // @Override
            // public int compare(IHundredBanker o1, IHundredBanker o2) {
            // return o1.getValue() - o2.getValue();
            // }
            // });
            boolean isIn = false;
            for (int i = 0; i < this.bankerList.size(); i++) {
                IHundredBanker hundredBanker = this.bankerList.get(i);
                if (hundredBanker == null) {
                    continue;
                }
                if (bankerPlayer.getValue() > hundredBanker.getValue()) {
                    this.bankerList.add(i, bankerPlayer);
                    isIn = true;
                    break;
                }
            }
            if (!isIn) {
                this.bankerList.add(bankerPlayer);
            }
        } else {
            this.bankerList.add(bankerPlayer);
        }

        return ErrorCode.OK;
    }

    @Override
    public ErrorCode downBanker(IPlayer player, int bankerUid) {
        if (bankerUid < 0) {
            Logs.ARENA.warn("%s 无法下庄, 无效下庄uid:%d", this, bankerUid);
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        if (null != this.curBanker && this.curBanker.getBankerUid() == bankerUid) {
            if (this.curBanker.getUid() != player.getUid()) {
                Logs.ARENA.warn("%s 无法下庄, 不能你自己", this);
                return ErrorCode.REQUEST_INVALID_DATA;
            }
            this.curBanker.down(true);
            return ErrorCode.OK;
        }

        for (int i = 0; i < this.bankerList.size(); i++) {
            IHundredBanker banker = this.bankerList.get(i);
            if (banker == null) {
                continue;
            }
            if (bankerUid == banker.getBankerUid()) {
                if (banker.getUid() != player.getUid()) {
                    Logs.ARENA.warn("%s 无法下庄, 不是你自己", this);
                    return ErrorCode.REQUEST_INVALID_DATA;
                }
                IBoxOwner boxOwner = getRoom().getBoxOwner();
                if (null != boxOwner) {
                    IClub mainClub = (IClub)boxOwner;
                    int finalScore = boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(player.getUid()),
                        player.getUid(), banker.getValue(), 0, false);
                }
                this.bankerList.remove(i);
                return ErrorCode.OK;
            }
        }
        Logs.ARENA.warn("%s 无法下庄, 无效下庄uid:%d", this, bankerUid);
        return ErrorCode.REQUEST_INVALID_DATA;
    }

    @Override
    public ErrorCode reb(IPlayer player, int index, int value, EHundredArenaRebType type) {
        IHundredPlayer hundredPlayer = (IHundredPlayer)this.allPlayer.get(player.getUid());
        if (null == hundredPlayer) {
            Logs.ARENA.warn("%s %s 不在竞技场中无法押注", this, player);
            return ErrorCode.PLAYER_ARENA_NOT_IN;
        }
        // 房间不在可押注的状态
        if (!this.isReb()) {
            return ErrorCode.ARENA_REB_FAIL;
        }
        if (index < 0 || index >= this.playerCnt) {
            Logs.ARENA.warn("%s %s 不能押注, 无效押方 index:%d", this, player, index);
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        // 下注值错误
        if (value <= 0) {
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        // 超过下注限制
        if (hundredPlayer.getScore(Score.HUNDRED_ALL_REB, false) + value * 100 > this.rebLimit * 100) {
            return ErrorCode.ARENA_HUNDRED_REB_LIMIT_10000;
        }
        value *= 100;
        ErrorCode err;
        synchronized (this) {
            err = this.checkReb(player, index, value, type);
            if (ErrorCode.OK != err) {
                Logs.ARENA.warn("%s %s 不能押注, 押注条件未满足", this, player);
                return err;
            }
            IBoxOwner boxOwner = getRoom().getBoxOwner();
            if (null == boxOwner) {
                return ErrorCode.REQUEST_INVALID;
            }
            IClub mainClub = (IClub)boxOwner;
            value = boxOwner.addMemberValueByBox(mainClub.getEnterFromClubUid(player.getUid()), player.getUid(), -value,
                0, false);
            if (value <= 0) {
                return ErrorCode.ARENA_LESS_THAN_MIN_VALUE;
            }
            this.allRebValue += value;
            hundredPlayer.addScore(Score.HUNDRED_ALL_REB, value, false);
            this.allReb[index].reb(type, player.getUid(), value);
        }
        this.allRebUid.add(player.getUid());

        // 累计每个下注区域的总值
        rebAllRecords[index - 1] += value;
        needNoticeAllRecord = true;
        // 如果是座位玩家，就直接广播
        if (this.isInSeat(player.getUid())) {
            this.notifyReb(player, index, type, value, false);// 下注信息通知所有人
        } else {
            this.notifyReb(player, index, type, value, true);// 下注信息只通知自己
        }

        hundredPlayer.setNoRebRound(0);

        return err;
    }

    /**
     * 是否是座位玩家
     * 
     * @param playerUid
     * @return
     */
    protected boolean isInSeat(long playerUid) {
        for (int i = 0; i < this.getVipSeats().length; i++) {
            if (this.getVipSeats()[i] == playerUid) {
                return true;
            }
        }
        return false;
    }
    
    protected abstract int getMul(int index);
    
   
}
