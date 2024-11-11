package com.xiuxiu.app.server.room.normal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.alibaba.fastjson.JSONObject;
import com.xiuxiu.algorithm.poker.EPokerCardType;
import com.xiuxiu.algorithm.poker.PokerUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.helper.PCLIClubNtfHelperInfo.HelperInfo;
import com.xiuxiu.app.protocol.client.room.PCLIBoxRoomInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomBriefInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfBeginBeforeInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfChangeStateInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfDissolveInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfKillInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfLeaveInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfLocationRelationInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfMemberNotGuest;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfOfflineInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfOnlineInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomPlayerInfo;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Main;
import com.xiuxiu.app.server.box.IBoxOwner;
import com.xiuxiu.app.server.chat.ChatManager;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.EDissolve;
import com.xiuxiu.app.server.room.EPlaceType;
import com.xiuxiu.app.server.room.ERoomDestroyType;
import com.xiuxiu.app.server.room.ERoomListState;
import com.xiuxiu.app.server.room.ERoomState;
import com.xiuxiu.app.server.room.ERoomType;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.handle.IBoxRoomHandle;
import com.xiuxiu.app.server.room.handle.IRoomHandle;
import com.xiuxiu.app.server.room.handle.impl.BoxArenaRoomHandle;
import com.xiuxiu.app.server.room.handle.impl.hundred.AbstractHundredRoomHandle;
import com.xiuxiu.app.server.room.handle.impl.hundred.LhdHundredRoomHandle;
import com.xiuxiu.app.server.room.normal.action.AutoStartAction;
import com.xiuxiu.app.server.room.normal.action.DissolveWaitAction;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.action.ReadyCountDownAction;
import com.xiuxiu.app.server.room.normal.action.ShowOffAction;
import com.xiuxiu.app.server.room.player.poker.cow.CowPlayer;
import com.xiuxiu.app.server.room.record.Record;
import com.xiuxiu.app.server.score.IRoomScore;
import com.xiuxiu.app.server.score.RoomScoreFactory;
import com.xiuxiu.app.server.table.GoodPoker;
import com.xiuxiu.app.server.table.GoodPoker.GoodPokerInfo;
import com.xiuxiu.app.server.table.GoodPokerManager;
import com.xiuxiu.core.ds.ConcurrentHashSet;
import com.xiuxiu.core.net.Task;
import com.xiuxiu.core.thread.BaseThread;
import com.xiuxiu.core.thread.Tick;
import com.xiuxiu.core.utils.AsyncTask;
import com.xiuxiu.core.utils.Location;
import com.xiuxiu.core.utils.NumberUtils;
import com.xiuxiu.core.utils.RandomUtil;

public abstract class Room implements IRoom, Tick {
    protected RoomInfo info;

    protected int bureau;
    protected int curBureau;
    protected int playerNum;
    protected int playerMinNum;

    protected ERoomType roomType;
    protected final Stack<IAction> action = new Stack<>();
    protected AtomicReference<ERoomState> roomState = new AtomicReference<>(ERoomState.NEW);
    protected IRoomPlayer[] allPlayer;
    protected AtomicInteger playerCnt = new AtomicInteger(0);
    protected ConcurrentHashSet<Long> ready = new ConcurrentHashSet<>();
    protected ConcurrentHashSet<Long> watchList = new ConcurrentHashSet<>();
    protected ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    protected boolean autoDestroy = true;
    protected boolean autoStart = false;
    protected int autoStartTime = 10000;   // ms
    protected boolean isOver = false;
    protected int timeout = -1;                                                     // 超时时间

    protected BaseThread currThread;

    protected int bankerIndex = -1;                                               // 庄家索引
    /**
     * 房间战绩
     */
    protected IRoomScore roomScore;
    /**
     * 回放数量
     */
    protected Record record;
    protected boolean detectionIP = false;                                      // IP检测开关(0 关闭 1 开启)

    protected int curPlayerCnt = 0;                                     // 当前打牌玩家
    protected LinkedList<Byte> allCard = new LinkedList<>();            // 所有牌
    protected AtomicBoolean destroyed = new AtomicBoolean(false);
    protected long isDestroyUid = -1;           // 申请投票解散玩家Uid
    protected ERoomDestroyType destroyType = ERoomDestroyType.UN_DESTROY;              // 解散状态
    protected boolean autoReady = true;             // 自动准备

    /**
     * 当前房间游戏桌玩家临时信息
     */
    protected Map<Long, Map<String, Integer>> temporaryPropertyMap = new ConcurrentHashMap<Long, Map<String, Integer>>();

    /**
     * 已经结束的局数
     */
    private int finishBureauCount = 0;

    /**
     * 玩家申请解散次数（2次）
     */
    protected Map<Long, Integer> dissolveMap = new ConcurrentHashMap<Long, Integer>();
    /** 房间相关业务扩展处理器 */
    private IRoomHandle roomHandle;
    
    protected Map<Integer, List<GoodPokerInfo>> goodPokerMap = new ConcurrentHashMap<Integer, List<GoodPokerInfo>>();
    
    /**
     * 发牌开始
     */
    //发牌开关(0 关闭 1 开启)
    protected boolean doDeal = false;
    //申请人位置
    protected List<Integer> goodPlayer = new ArrayList<Integer>();
    
    public boolean isDoDeal() {
		return doDeal;
	}

	public void setDoDeal(boolean doDeal) {
		this.doDeal = doDeal;
	}
	
	public List<Integer> getGoodPlayer() {
		return goodPlayer;
	}

	public void setGoodPlayer(List<Integer> goodPlayer) {
		this.goodPlayer = goodPlayer;
	}

	public void addGoodPlayer(Integer weizi) {
		goodPlayer.add(weizi);
	}
	
	public void clearGoodPlayer() {
		goodPlayer.clear();
	}
	
	/**
     * 发牌结束
     */
	
	public Room(RoomInfo info) {
        this(info, ERoomType.NORMAL);
    }

    public Room(RoomInfo info, ERoomType roomType) {
        this.info = info;
        this.roomType = roomType;
    }

    public void init() {
        this.roomScore = RoomScoreFactory.createRoomScore(this);
        this.bureau = this.info.getRule().getOrDefault(RoomRule.RR_BUREAU, 8);
        this.playerNum = this.info.getRule().getOrDefault(RoomRule.RR_PLAYER_NUM, 3);
        this.playerMinNum = this.info.getRule().getOrDefault(RoomRule.RR_PLAYER_MIN_NUM, this.playerNum);

        this.currThread = Main.I.getRoomMessageProcess().getThread(this.info.getUid(),this.getGameType() == GameType.GAME_TYPE_HUNDRED_LHD||this.getGameType() == GameType.GAME_TYPE_HUNDRED_BACCARAT);
        this.currThread.attackTick(this);
        this.allPlayer = new IRoomPlayer[this.playerNum];
        Map<Integer, List<GoodPokerInfo>> tempMap = GoodPokerManager.I.getGoodPokerByGameType(getGameType());
        if (tempMap != null) {
        	Map<Integer, List<GoodPokerInfo>> sourceMap = new ConcurrentHashMap<Integer, List<GoodPokerInfo>>();
        	Iterator<Entry<Integer, List<GoodPokerInfo>>> iter = tempMap.entrySet().iterator();
        	while (iter.hasNext()) {
        		Map.Entry<Integer, List<GoodPokerInfo>> entry = iter.next();
    			List<GoodPokerInfo> tempList = null;
    			if (sourceMap.containsKey(entry.getKey())) {
    				tempList = tempMap.get(entry.getKey());
    			} else {
    				tempList = new ArrayList<GoodPoker.GoodPokerInfo>();
    				sourceMap.put(entry.getKey(), tempList);
    			}
    			tempList.addAll(entry.getValue());
        	}
        	this.goodPokerMap.putAll(sourceMap);
        }

        this.changeState(ERoomListState.NEW);
        this.getRoomHandle().init();
    }

    @Override
    public void start() {
        if (!this.action.empty()) {
            if (this.action.peek() instanceof AutoStartAction || this.action.peek() instanceof ReadyCountDownAction) {
                this.action.pop();
            }
        }
        if (ERoomState.NEW == this.roomState.get()) {
            this.changeState(ERoomListState.START);
            this.firstStart();
        }
        if (this.roomState.compareAndSet(ERoomState.NEW, ERoomState.START)) {
            Logs.ROOM.debug("%s 房间开始", this);
            this.start0();
        } else if (this.roomState.compareAndSet(ERoomState.AGAIN, ERoomState.START)) {
            Logs.ROOM.debug("%s 房间新局", this);
            this.start0();
        } else if (this.roomState.compareAndSet(ERoomState.AUTO_START, ERoomState.START)) {
            Logs.ROOM.debug("%s 房间新局, 自动开始", this);
            for (int i = 0; i < this.playerNum; ++i) {
                IRoomPlayer player = this.allPlayer[i];
                if (null == player || player.isGuest()) {
                    continue;
                }
                this.ready.add(player.getUid());
            }
            this.start0();
        } else {
            Logs.ROOM.warn("%s 房间状态不对无法开启", this);
        }
    }

    /**
     * 开始
     */
    private void start0() {
        this.curPlayerCnt = this.playerCnt.get();
        ++this.curBureau;
        this.info.setCurBureau(this.curBureau);
        this.clear();
        this.doStart0();
        this.doShuffle();
        this.doGoodPoker();
        this.doDeal();
        this.doSendGameStart();
        this.doBeginRecord();
        this.doStart1();
        this.info.setDirty(true);
        this.getRoomHandle().start();
        //this.checkGroupQuest();
    }
    
    public boolean autoStart() {
        if (this.autoStart) {
            if (this.roomState.compareAndSet(ERoomState.NEW, ERoomState.AUTO_START) || this.roomState.compareAndSet(ERoomState.AGAIN, ERoomState.AUTO_START)) {
                AutoStartAction autoStartAction = new AutoStartAction(this, this.autoStartTime);
                this.addAction(autoStartAction);
                PCLIRoomNtfBeginBeforeInfo beginBeforeInfo = new PCLIRoomNtfBeginBeforeInfo();
                // TODO modify ms
                beginBeforeInfo.beginRemain = this.autoStartTime / 1000;
                this.broadcast2Client(CommandId.CLI_NTF_ROOM_BEGIN_BEFORE, beginBeforeInfo);
                return true;
            }
        }
        return false;
    }

    @Override
    public void checkStart() {
        if (this.playerCnt.get() == this.ready.size()) {
            if (this.ready.size() >= this.playerMinNum) {
                this.getRoomHandle().startBefore();
                this.start();
            }
        }
    }

    protected void firstStart() {

    }

    private void doStart0() {
        PCLIRoomNtfMemberNotGuest info = new PCLIRoomNtfMemberNotGuest();
        ReentrantReadWriteLock.ReadLock readLock = this.rwLock.readLock();
        try {
            if (readLock.tryLock() || readLock.tryLock(1, TimeUnit.MINUTES)) {
                for (int i = 0; i < this.playerNum; ++i) {
                    IRoomPlayer player = this.allPlayer[i];
                    if (null == player || player.isGuest()) {
                        continue;
                    }
                    info.notGuestMembers.add(player.getUid());
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }
        if (info.notGuestMembers.size() > 0) {
            this.broadcast2Client(CommandId.CLI_NTF_ROOM_MEMBER_NOT_GUEST, info);
        }
        for (int j = 0, len = this.allPlayer.length; j < len; ++j) {
            IRoomPlayer player = this.allPlayer[j];
            if (null == player || player.isGuest()) {
                continue;
            }
            player.doStart();
        }
    }

    /**
     * 洗牌
     */
    protected abstract void doShuffle();
    
    /**
     * 发牌
     */
    protected abstract void doDeal();
    
    /**
     * 开始记录
     */
    protected void doBeginRecord() {
    }

    /**
     * 开始
     */
    protected abstract void doStart1();

    @Override
    public void stop() {
        if (this.roomState.compareAndSet(ERoomState.START, ERoomState.STOP)) {
            ++finishBureauCount;
            Logs.ROOM.debug("%s 房间结束一局", this);
            if (this.checkAgain() && this.getRoomHandle().checkAgain(Boolean.TRUE)) {
                this.doSendGameOver(true);
                this.sendGameCurBureauFinish(true);
                this.savePrevInfo();
                this.clear();
                this.again();
                this.autoStart();
                this.readyCountDown();
            } else {
                this.finish();
                this.doSendGameOver(false);
                this.sendGameCurBureauFinish(false);
                this.savePrevInfo();
                this.clear();
                if (this.autoDestroy) {
                    this.destroy();
                }
            }
            this.curPlayerCnt = 0;
        }
    }

    @Override
    public void again() {
        if (this.roomState.compareAndSet(ERoomState.STOP, ERoomState.AGAIN)) {
            getRoomHandle().again();
        }
    }

    protected boolean checkAgain() {
        if (this.getRoomHandle() instanceof BoxArenaRoomHandle) {
            return true;
        }
        Integer maxLostScore = this.info.getRule().getOrDefault(RoomRule.RR_TOP, -1);
        if (-1 == maxLostScore) {
            return this.curBureau < this.bureau;
        }
        if (this.bureau == this.curBureau) {
            return false;
        }
        int min = -maxLostScore;
        ReentrantReadWriteLock.ReadLock readLock = this.rwLock.readLock();
        try {
            if (readLock.tryLock() || readLock.tryLock(1, TimeUnit.MINUTES)) {
                for (int i = 0; i < this.playerNum; ++i) {
                    IRoomPlayer other = this.allPlayer[i];
                    if (null == other || other.isGuest()) {
                        continue;
                    }
                    int score = other.getScore(Score.ACC_TOTAL_SCORE, true) / 100;
                    if (score >= 0) {
                        continue;
                    }
                    if (score < min) {
                        this.setDestroyType(ERoomDestroyType.TOP_DESTROY);
                        this.setIsDestroyUid(other.getUid());
                        return false;
                    }
                }
            } else {
                return false;
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }
        return true;
    }

    @Override
    public void clearGuest(IRoomPlayer player) {
        player.setGuest(false);
        this.watchList.remove(player.getUid());
    }

    @Override
    public void finish() {
        if (this.roomState.compareAndSet(ERoomState.STOP, ERoomState.FINISH)) {
            Logs.ROOM.debug("%s 房间完成", this);
            this.doFinish(true, true);
            this.getRoomHandle().doFinishAfter(true, true);
            this.curPlayerCnt = 0;
        }
    }

    protected abstract void doFinish(boolean isNormal, boolean isNewBureau);

    @Override
    public void destroy() {
        if (ERoomState.DESTROY == this.roomState.get()) {
            Logs.ROOM.debug("%s 房间已经销毁", this);
            return;
        }
        boolean isDissolve = false;
        boolean notify = true;
        if (this.roomState.compareAndSet(ERoomState.NEW, ERoomState.DESTROY)) {
            Logs.ROOM.debug("%s 新房间销毁", this);
            this.changeState(ERoomListState.DEL);
            isDissolve = true;
        } else if (this.roomState.compareAndSet(ERoomState.START, ERoomState.DESTROY)) {
            Logs.ROOM.debug("%s 进行中房间销毁", this);
            this.doFinish(false, false);
            this.getRoomHandle().doFinishAfter(false, false);
            this.doSendGameOver(false);
            this.sendGameCurBureauFinish(false);
            isDissolve = true;
            notify = false;
        } else if (this.roomState.compareAndSet(ERoomState.AGAIN, ERoomState.DESTROY)) {
            Logs.ROOM.debug("%s 再来一局房间销毁", this);
            this.doFinish(false, true);
            this.getRoomHandle().doFinishAfter(false, true);
            this.doSendGameOver(false);
            this.sendGameCurBureauFinish(false);
            isDissolve = true;
            notify = false;
        } else if (this.roomState.compareAndSet(ERoomState.AUTO_START, ERoomState.DESTROY)) {
            Logs.ROOM.debug("%s 自动开始房间销毁", this);
            this.doFinish(false, true);
            this.getRoomHandle().doFinishAfter(false, true);
            this.doSendGameOver(false);
            this.sendGameCurBureauFinish(false);
            isDissolve = true;
            notify = false;
        } else {
            Logs.ROOM.debug("%s 房间正常销毁", this);
            this.roomState.set(ERoomState.DESTROY);
            notify = false;
        }
        if (isDissolve && ERoomType.NORMAL == this.roomType) {
            // 解散
            for (int i = 0; i < this.playerNum; ++i) {
                IRoomPlayer player = this.allPlayer[i];
                if (null == player) {
                    continue;
                }
                if (player.getUid() == this.info.getOwnerPlayerUid()) {
                    ChatManager.I.notifyRoomDissolveWithOwner(player.getUid(), this.info.getGameType(), this.info.getGameSubType(), this.curBureau, this.bureau, this.info.getCost() * (this.bureau - this.curBureau) / this.bureau, this.getRoomId());
                } else {
                    if (player.isOffline()) {
                        ChatManager.I.notifyRoomDissolve(player.getUid(), this.info.getGameType(), this.info.getGameSubType(), this.getRoomId());
                    }
                }
            }
        }
        // 大局抽水
        // getRoomHandle().destoryGoldHandle();

        this.killAll(true, notify);
        this.watchList.clear();
        this.doDestroy();
        this.curPlayerCnt = 0;
        this.clear();
        this.info.setDirty(true);
        this.save();
        this.currThread.deAttackTick(this);
        RoomManager.I.removeRoom(this);
        getRoomHandle().destoryAfter();
        this.doDestoryAfter();
    }

    protected void doDestoryAfter() {

    }

    protected void doDestroy() {
        if (this.destroyed.compareAndSet(false, true)) {
            getRoomHandle().doDestroy();
        }
    }

    @Override
    public ErrorCode kill(Player player, long killPlayerUid) {
        if (!this.isOwner(player.getUid())) {
            Logs.ROOM.warn("%s %s 不是房间拥有着, 无法踢人", this, player);
            return ErrorCode.ROOM_NOT_OWNER;
        }
        if (ERoomState.NEW != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经开始, 无法踢人", this, player);
            return ErrorCode.ROOM_ALREADY_START;
        }
        if (player.getUid() == killPlayerUid) {
            Logs.ROOM.warn("%s %s 不能踢自己, 无法踢人", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        IRoomPlayer killRoomPlayer = this.getRoomPlayer(killPlayerUid);
        if (null == killRoomPlayer) {
            Logs.ROOM.warn("%s %d 不在房间里", this, killPlayerUid);
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        this.ready.remove(player.getUid());
        killRoomPlayer.getPlayer().changeRoomId(-1, -1);
        if (null != this.allPlayer[killRoomPlayer.getIndex()]) {
            if (this.playerNum == this.playerCnt.getAndDecrement()) {
                this.changeState(ERoomListState.CAN_ADD);
            }
        }

        ReentrantReadWriteLock.WriteLock writeLock = this.rwLock.writeLock();
        try {
            if (writeLock.tryLock() || writeLock.tryLock(1, TimeUnit.MINUTES)) {
                this.allPlayer[killRoomPlayer.getIndex()] = null;
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (writeLock.isHeldByCurrentThread()) {
                writeLock.unlock();
            }
        }
        PCLIRoomNtfKillInfo killInfo = new PCLIRoomNtfKillInfo();
        killInfo.roomId = this.getRoomId();
        killInfo.state = 1;
        killRoomPlayer.send(CommandId.CLI_NTF_ROOM_KILL, killInfo);

        PCLIRoomNtfLeaveInfo leaveInfo = new PCLIRoomNtfLeaveInfo();
        leaveInfo.roomId = this.info.getRoomId();
        leaveInfo.playerUid = killPlayerUid;
        this.broadcast2Client(CommandId.CLI_NTF_ROOM_LEAVE, leaveInfo);

        this.checkStart();
        return ErrorCode.OK;
    }

    public void killAll(boolean destroy, boolean notify) {
        List<Long> killPlayerUids = null;
        ReentrantReadWriteLock.WriteLock writeLock = this.rwLock.writeLock();
        try {
            if (notify) {
                PCLIRoomNtfKillInfo info = new PCLIRoomNtfKillInfo();
                info.roomId = this.getRoomId();
                info.state = destroy ? 2 : 1;
                this.broadcast2Client(CommandId.CLI_NTF_ROOM_KILL, info);
            }

            if (writeLock.tryLock() || writeLock.tryLock(1, TimeUnit.MINUTES)) {
                for (int i = 0; i < this.playerNum; ++i) {
                    IRoomPlayer player = this.allPlayer[i];
                    if (null != player) {
                        player.setRoom(null);
                        if (null == killPlayerUids) {
                            killPlayerUids = new ArrayList<Long>();
                        }
                        killPlayerUids.add(player.getUid());
                    }
                    this.allPlayer[i] = null;
                }
                if (null == killPlayerUids) {
                    killPlayerUids = new ArrayList<Long>();
                }
                for (long uid : this.watchList) {
                    killPlayerUids.add(uid);
                }
                this.playerCnt.set(0);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (writeLock.isHeldByCurrentThread()) {
                writeLock.unlock();
            }
        }
        Iterator<Long> it = this.watchList.iterator();
        while (it.hasNext()) {
            Player player = PlayerManager.I.getPlayer(it.next());
            if (null != player) {
                player.changeRoomId(-1, -1);
            }
        }
        if (killPlayerUids != null) {
            getRoomHandle().killAll(killPlayerUids);
        }
    }

    @Override
    public ErrorCode ready(Player player) {
        if (ERoomState.NEW != this.roomState.get() && ERoomState.AGAIN != this.roomState.get() && ERoomState.AUTO_START != this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间已经开始或结束, 无法准备", this, player);
            return ErrorCode.REQUEST_INVALID;
        }

        IRoomPlayer roomPlayer = this.getRoomPlayer(player.getUid());
        if (null == roomPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里", this, roomPlayer);
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        if (roomPlayer.isGuest()) {
            Logs.ROOM.warn("%s %s 游客无法准备", this, roomPlayer);
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        ErrorCode erroCode = getRoomHandle().readyHandle(player.getUid(), Boolean.TRUE);
        if (erroCode == ErrorCode.OK) {
            this.getRoomHandle().startCheckLeave();
            this.sendReadyCountDown(player);
        }
        return erroCode;
    }

    
    @Override
    public void addReadyPlayerUid(long playerUid) {
        this.ready.add(playerUid);
    }

    @Override
    public ErrorCode dissolve(Player player, boolean force) {
        ErrorCode tempErrorCode = getRoomHandle().canDissolve(player);
        if (ErrorCode.OK != tempErrorCode) {
            return tempErrorCode;
        }
        if (force) {
            Logs.ROOM.warn("%s %s 强制解散", this, player);
            this.destroy();
            return ErrorCode.OK;
        }
        if (ERoomState.NEW == this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间还没开始, 无法解散", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (!this.action.isEmpty()) {
            IAction action = this.action.peek();
            if (action instanceof DissolveWaitAction) {
                Logs.ROOM.warn("%s %s 房间正在解散中, 无法解散", this, player);
                return ErrorCode.REQUEST_INVALID;
            }
        }

        IRoomPlayer roomPlayer = getRoomPlayer(player.getUid());
        if (null == roomPlayer || roomPlayer.isGuest()) {
            return ErrorCode.REQUEST_INVALID;
        }
        long uid = player.getUid();
        this.dissolveMap.put(uid, this.dissolveMap.containsKey(uid) ? this.dissolveMap.get(uid) + 1 : 1);
        int count = this.dissolveMap.get(player.getUid());
        if (count > 2) {
            Logs.ROOM.warn("%s %s 申请解散次数上限, 无法解散", this, player);
            return ErrorCode.ROOM_DISSOLVE_MAX;
        }
        DissolveWaitAction dissolveWaitAction = new DissolveWaitAction(this, Constant.ROOM_DISSOLVE_WAIT_TIME);
        for (int i = 0; i < this.playerNum; ++i) {
            IRoomPlayer temp = this.allPlayer[i];
            if (null == temp || temp.isGuest()) {
                continue;
            }
            if (player.getUid() != temp.getUid()) {
                dissolveWaitAction.addPlayer(temp.getUid());
            }
        }
        dissolveWaitAction.addPlayerDissolve(player.getUid());
        this.addAction(dissolveWaitAction);
        PCLIRoomNtfDissolveInfo info = new PCLIRoomNtfDissolveInfo();
        info.roomId = this.getRoomId();
        info.dissolvePlayerUid = player.getUid();
        this.isDestroyUid = player.getUid();
        this.broadcast2Client(CommandId.CLI_NTF_ROOM_DISSOLVE, info, false);
        return ErrorCode.OK;
    }

    @Override
    public ErrorCode dissolveOperate(Player player, EDissolve op) {
        if (ERoomState.NEW == this.roomState.get()) {
            Logs.ROOM.warn("%s %s 房间还没开始, 无法解散操作", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (this.action.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法解散操作", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        IAction action = this.action.peek();
        if (action instanceof DissolveWaitAction) {
            ErrorCode err = ErrorCode.REQUEST_INVALID;
            if (EDissolve.AGREE == op) {
                // 同意
                err = ((DissolveWaitAction) action).playerSelect(player.getUid(), EActionOp.DISSOLVE_AGREE);
                this.setDestroyType(ERoomDestroyType.APPLY_DESTROY);
            } else if (EDissolve.REJECT == op) {
                this.isDestroyUid = -1;
                // 拒绝
                err = ((DissolveWaitAction) action).playerSelect(player.getUid(), EActionOp.DISSOLVE_REJECT);
                this.setDestroyType(ERoomDestroyType.UN_DESTROY);
            }
            if (ErrorCode.OK == err) {
                this.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s %s 不能解散操作", this, player);
        return ErrorCode.REQUEST_INVALID;
    }

    @Override
    public ErrorCode join(Player player) {
        return getRoomHandle().join(player);
    }

    @Override
    public ErrorCode changeSeate(Player player,int newSeatIndex){
        if (newSeatIndex < 0 || newSeatIndex >= this.playerNum) {
            Logs.ROOM.warn("%s %s 位置无效:%d", this, player, newSeatIndex);
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        if (ERoomState.NEW != this.roomState.get()) {
            return ErrorCode.ROOM_ALREADY_START;
        }
        IRoomPlayer roomPlayer = this.getRoomPlayer(newSeatIndex);
        if (null != roomPlayer) {
            Logs.ROOM.warn("%s %s 位置:%d 已经有人了, 无法坐下", this, player, newSeatIndex);
            return ErrorCode.ROOM_INDEX_NOT_EMPTY;
        }

        roomPlayer = this.getRoomPlayer(player.getUid());
        if (null == roomPlayer) {
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }

        if (this.playerNum == playerCnt.get()) {
            Logs.ROOM.warn("%s %s 房间已满, 无法坐下", this, player);
            return ErrorCode.ROOM_PLAYER_FULL;
        }

        ReentrantReadWriteLock.WriteLock writeLock = this.rwLock.writeLock();
        try {
            if (writeLock.tryLock() || writeLock.tryLock(1, TimeUnit.MINUTES)) {
                if (null != this.allPlayer[newSeatIndex]) {
                    Logs.ROOM.warn("%s %s 位置:%d 已经有人了, 无法坐下", this, player, newSeatIndex);
                    return ErrorCode.ROOM_INDEX_NOT_EMPTY;
                }
                int oldSeat = roomPlayer.getIndex();
                roomPlayer.setIndex(newSeatIndex);
                this.allPlayer[newSeatIndex] = roomPlayer;
                this.allPlayer[oldSeat] = null;
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (writeLock.isHeldByCurrentThread()) {
                writeLock.unlock();
            }
        }
        this.broadcast2Client(CommandId.CLI_NTF_ROOM_INFO, this.getRoomInfo());
        return ErrorCode.OK;
    }

    @Override
    public ErrorCode sitDown(IPlayer player, int index) {
        if (index < 0 || index >= this.playerNum) {
            Logs.ROOM.warn("%s %s 位置无效:%d", this, player, index);
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        if (ERoomState.NEW != this.roomState.get() && ERoomState.FINISH != this.roomState.get() && ERoomState.DESTROY != this.roomState.get()) {
            IRoomPlayer roomPlayer = this.getRoomPlayer(index);
            if (null != roomPlayer) {
                Logs.ROOM.warn("%s %s 位置:%d 已经有人了, 无法坐下", this, player, index);
                return ErrorCode.ROOM_INDEX_NOT_EMPTY;
            }
            roomPlayer = this.getRoomPlayer(player.getUid());
            if (null != roomPlayer) {
                Logs.ROOM.warn("%s %s 已经在房间里了, 无法坐下", this, player);
                return ErrorCode.PLAYER_ROOM_IN;
            }
            if (this.playerNum == playerCnt.get()) {
                Logs.ROOM.warn("%s %s 房间已满, 无法坐下", this, player);
                return ErrorCode.ROOM_PLAYER_FULL;
            }
            if (!this.watchList.contains(player.getUid())) {
                Logs.ROOM.warn("%s %s 不在该房间围观, 无法坐下", this, player);
                return ErrorCode.ROOM_NOT_WATCH;
            }
            ReentrantReadWriteLock.WriteLock writeLock = this.rwLock.writeLock();
            try {
                if (writeLock.tryLock() || writeLock.tryLock(1, TimeUnit.MINUTES)) {
                    if (null != this.allPlayer[index]) {
                        Logs.ROOM.warn("%s %s 位置:%d 已经有人了, 无法坐下", this, player, index);
                        return ErrorCode.ROOM_INDEX_NOT_EMPTY;
                    }
                    roomPlayer = this.createPlayer();
                    roomPlayer.setPlayer(player);
                    roomPlayer.setIndex(index);
                    roomPlayer.setGuest(this.isStart());
                    if (!roomPlayer.isGuest()) {
                        this.watchList.remove(player.getUid());
                    }
                    this.allPlayer[index] = roomPlayer;
                    this.playerCnt.incrementAndGet();
                    if (this.playerNum == this.playerCnt.get()) {
                        this.changeState(ERoomListState.FULL);
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if (writeLock.isHeldByCurrentThread()) {
                    writeLock.unlock();
                }
            }
            this.broadcast2Client(CommandId.CLI_NTF_ROOM_INFO, this.getRoomInfo());
            return ErrorCode.OK;
        }
        Logs.ROOM.debug("%s %s 房间结束, 无法坐下", this, player);
        return ErrorCode.ROOM_ALREADY_START;
    }

    @Override
    public ErrorCode sitDown(IRoomPlayer player, int index) {
        if (index < 0 || index >= this.playerNum) {
            Logs.ROOM.warn("%s %s 位置无效:%d", this, player, index);
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        if (ERoomState.FINISH != this.roomState.get() && ERoomState.DESTROY != this.roomState.get()) {
            IRoomPlayer roomPlayer = this.getRoomPlayer(index);
            if (null != roomPlayer) {
                Logs.ROOM.warn("%s %s 位置:%d 已经有人了, 无法坐下", this, player, index);
                return ErrorCode.ROOM_INDEX_NOT_EMPTY;
            }
            roomPlayer = this.getRoomPlayer(player.getUid());
            if (null != roomPlayer) {
                Logs.ROOM.warn("%s %s 已经在房间里了, 无法坐下", this, player);
                return ErrorCode.PLAYER_ROOM_IN;
            }
            if (this.playerNum == playerCnt.get()) {
                Logs.ROOM.warn("%s %s 房间已满, 无法坐下", this, player);
                return ErrorCode.ROOM_PLAYER_FULL;
            }
            if (!this.watchList.contains(player.getUid())) {
                Logs.ROOM.warn("%s %s 不在该房间围观, 无法坐下", this, player);
                return ErrorCode.ROOM_NOT_WATCH;
            }
            ReentrantReadWriteLock.WriteLock writeLock = this.rwLock.writeLock();
            try {
                if (writeLock.tryLock() || writeLock.tryLock(1, TimeUnit.MINUTES)) {
                    if (null != this.allPlayer[index]) {
                        Logs.ROOM.warn("%s %s 位置:%d 已经有人了, 无法坐下", this, player, index);
                        return ErrorCode.ROOM_INDEX_NOT_EMPTY;
                    }
                    roomPlayer = player;
                    roomPlayer.setIndex(index);
                    roomPlayer.setGuest(this.isStart());
                    if (!roomPlayer.isGuest()) {
                        this.watchList.remove(player.getUid());
                    }
                    this.allPlayer[index] = roomPlayer;
                    this.playerCnt.incrementAndGet();
                    if (this.playerNum == this.playerCnt.get()) {
                        this.changeState(ERoomListState.FULL);
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if (writeLock.isHeldByCurrentThread()) {
                    writeLock.unlock();
                }
            }
            this.broadcast2Client(CommandId.CLI_NTF_ROOM_INFO, this.getRoomInfo());
            return ErrorCode.OK;
        }
        Logs.ROOM.debug("%s %s 房间结束, 无法坐下", this, player);
        return ErrorCode.ROOM_ALREADY_START;
    }

    @Override
    public ErrorCode leave(Player player) {
        return this.getRoomHandle().leave(player);
    }

    @Override
    public ErrorCode sitUp(IPlayer player) {
        IRoomPlayer roomPlayer = this.getRoomPlayer(player.getUid());
        if (null == roomPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里, 无法站起", this, player);
            return ErrorCode.ROOM_INDEX_NOT_EMPTY;
        }
        if (roomPlayer.isGuest()) {
            ReentrantReadWriteLock.WriteLock writeLock = this.rwLock.writeLock();
            try {
                if (writeLock.tryLock() || writeLock.tryLock(1, TimeUnit.MINUTES)) {
                    if (null != this.allPlayer[roomPlayer.getIndex()]) {
                        if (this.playerNum == this.playerCnt.getAndDecrement()) {
                            this.changeState(ERoomListState.CAN_ADD);
                        }
                    }
                    this.allPlayer[roomPlayer.getIndex()] = null;

                    this.broadcast2Client(CommandId.CLI_NTF_ROOM_INFO, this.getRoomInfo());
                    this.watchList.add(player.getUid());
                }
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if (writeLock.isHeldByCurrentThread()) {
                    writeLock.unlock();
                }
            }

            player.send(CommandId.CLI_NTF_ROOM_INFO, this.getRoomInfo());
            return ErrorCode.OK;
        }
        if (ERoomState.AUTO_START == this.roomState.get() || ERoomState.STOP == this.roomState.get() || ERoomState.AGAIN == this.roomState.get()) {
            ReentrantReadWriteLock.WriteLock writeLock = this.rwLock.writeLock();
            try {
                if (writeLock.tryLock() || writeLock.tryLock(1, TimeUnit.MINUTES)) {            
                    this.ready.remove(player.getUid());
                    if (null != this.allPlayer[roomPlayer.getIndex()]) {
                        if (this.playerNum == this.playerCnt.getAndDecrement()) {
                            this.changeState(ERoomListState.CAN_ADD);
                        }
                    }
                    this.allPlayer[roomPlayer.getIndex()] = null;

                    this.broadcast2Client(CommandId.CLI_NTF_ROOM_INFO, this.getRoomInfo());
                    this.watchList.add(player.getUid());
                }
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if (writeLock.isHeldByCurrentThread()) {
                    writeLock.unlock();
                }
            }

            player.send(CommandId.CLI_NTF_ROOM_INFO, this.getRoomInfo());

            this.checkStart();

            return ErrorCode.OK;
        }
        Logs.ROOM.debug("%s %s 房间已经开始或结束, 无法站起", this, player);
        return ErrorCode.ROOM_ALREADY_START;
    }

    @Override
    public ErrorCode sitUp(IRoomPlayer player, boolean force) {
        IRoomPlayer roomPlayer = this.getRoomPlayer(player.getUid());
        if (null == roomPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里, 无法站起", this, player);
            return ErrorCode.ROOM_INDEX_NOT_EMPTY;
        }
        if (roomPlayer.isGuest()) {
            boolean flag = Boolean.FALSE;
            ReentrantReadWriteLock.WriteLock writeLock = this.rwLock.writeLock();
            try {
                if (writeLock.tryLock() || writeLock.tryLock(1, TimeUnit.MINUTES)) {
                    if (null != this.allPlayer[roomPlayer.getIndex()]) {
                        if (this.playerNum == this.playerCnt.getAndDecrement()) {
                            this.changeState(ERoomListState.CAN_ADD);
                        }
                    }
                    this.allPlayer[roomPlayer.getIndex()] = null;
                    this.ready.remove(player.getUid());
                    this.broadcast2Client(CommandId.CLI_NTF_ROOM_INFO, this.getRoomInfo());
                    this.watchList.add(player.getUid());
                    player.send(CommandId.CLI_NTF_ROOM_INFO, this.getRoomInfo());
                    flag = Boolean.TRUE;
                }
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if (writeLock.isHeldByCurrentThread()) {
                    writeLock.unlock();
                }
            }

            if (flag) {
                this.getRoomHandle().onSitup();
            }
            return ErrorCode.OK;
        }
        if (ERoomState.START != this.roomState.get()) {
            boolean flag = Boolean.FALSE;
            ReentrantReadWriteLock.WriteLock writeLock = this.rwLock.writeLock();
            try {
                if (writeLock.tryLock() || writeLock.tryLock(1, TimeUnit.MINUTES)) {
                    this.ready.remove(player.getUid());
                    if (null != this.allPlayer[roomPlayer.getIndex()]) {
                        if (this.playerNum == this.playerCnt.getAndDecrement()) {
                            this.changeState(ERoomListState.CAN_ADD);
                        }
                    }
                    this.allPlayer[roomPlayer.getIndex()] = null;

                    this.broadcast2Client(CommandId.CLI_NTF_ROOM_INFO, this.getRoomInfo());
                    this.watchList.add(player.getUid());
                    player.send(CommandId.CLI_NTF_ROOM_INFO, this.getRoomInfo());
                    flag = Boolean.TRUE;
                }
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if (writeLock.isHeldByCurrentThread()) {
                    writeLock.unlock();
                }
            }
            if (flag) {
                this.getRoomHandle().onSitup();
            }
            return ErrorCode.OK;
        }
        Logs.ROOM.debug("%s %s 房间已经开始或结束, 无法站起", this, player);
        return ErrorCode.ROOM_ALREADY_START;
    }

    @Override
    public ErrorCode showOff(IPlayer player) {
        IRoomPlayer roomPlayer = this.getRoomPlayer(player.getUid());
        if (null == roomPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里", this, player);
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        if (!this.isStart()) {
            Logs.ROOM.warn("%s %s 房间还没开始, 无法炫耀", this, player);
            return ErrorCode.ROOM_NOT_START;
        }
        if (!this.action.isEmpty()) {
            IAction action = this.action.peek();
            if (action instanceof ShowOffAction) {
                ErrorCode err = ((ShowOffAction) action).showOff(roomPlayer);
                if (ErrorCode.OK == err) {
                    this.tick();
                }
                return err;
            }
        }
        return ErrorCode.REQUEST_INVALID;
    }

    @Override
    public ErrorCode onShowOff(IRoomPlayer player) {
        return ErrorCode.GM_NOT_SUPPORT;
    }

    @Override
    public void doShowOffOver() {

    }

    @Override
    public ErrorCode changeState(Player player, EState state) {
        IRoomPlayer roomPlayer = this.getRoomPlayer(player.getUid());
        if (null == roomPlayer) {
            if (EState.ONLINE != state && this.watchList.remove(player.getUid())) {
                player.changeRoomId(-1, -1);
                return ErrorCode.OK;
            }
            Logs.ROOM.warn("%s 不在房间里", player);
            return ErrorCode.ROOM_NOT_EXISTS;
        }
        return this.changeState(roomPlayer, state);
    }

    @Override
    public ErrorCode changeState(IRoomPlayer player, EState state) {
        if (player.changeState(state)) {
            if (EState.ONLINE == state) {
                this.doOnline(player);
                PCLIRoomNtfOnlineInfo info = new PCLIRoomNtfOnlineInfo();
                info.playerUid = player.getUid();
                this.broadcast2Client(CommandId.CLI_NTF_ROOM_ONLINE, info);
            } else {
                this.doOffline(player);
                PCLIRoomNtfOfflineInfo info = new PCLIRoomNtfOfflineInfo();
                info.playerUid = player.getUid();
                this.broadcast2Client(CommandId.CLI_NTF_ROOM_OFFLINE, info);
            }
            return ErrorCode.OK;
        }
        return ErrorCode.REQUEST_INVALID_DATA;
    }

    protected void doOnline(IRoomPlayer player) {
        Logs.ROOM.debug("%s 上线", player);
        this.syncDeskInfo(player.getPlayer());
        if (!this.action.isEmpty()) {
            this.action.peek().online(player);
        }
    }

    protected void doOffline(IRoomPlayer player) {
        if (!this.action.isEmpty()) {
            this.action.peek().offline(player);
        }
    }

    /*
     * 观察者同步桌子信息
     */
    @Override
    public void watchPlayerSyncDeskInfo(IRoomPlayer player) {
        syncDeskInfo(player.getPlayer());
    }

    /**
     * 同步牌桌信息
     *
     * @param player
     */
    public abstract void syncDeskInfo(IPlayer player);

    /**
     * 发送开始
     */
    protected abstract void doSendGameStart();

    /**
     * 发送结束
     *
     * @param next
     */
    protected abstract void doSendGameOver(boolean next);

    /**
     * 发送当前局结束
     */
    protected void sendGameCurBureauFinish(boolean next) {
        this.broadcast2Client(CommandId.CLI_NTF_ROOM_GAME_FINISH, null);
    }

    @Override
    public ErrorCode doHandler(EActionOp op, Object info) {
        return ErrorCode.OK;
    }

    /**
     * 同步当前状态
     *
     * @param player
     */
    public void syncCurState(IPlayer player) {
        IRoomPlayer roomPlayer = this.getRoomPlayer(player.getUid());
        if (null == roomPlayer) {
            return;
        }
        if (!this.action.isEmpty()) {
            this.action.peek().online(roomPlayer);
        }
    }

    public void replaceHandCard(IRoomPlayer player, int card) {

    }

    @Override
    public void clear() {
        for (int i = 0; i < this.playerNum; ++i) {
            if (null != this.allPlayer[i]) {
                this.allPlayer[i].clear();
            }
        }
        this.action.clear();
        this.ready.clear();
        this.allCard.clear();
        this.isOver = false;
        this.record = null;
    }

    @Override
    public void clearByArenaOver() {
        for (int i = 0; i < this.playerNum; ++i) {
            if (null != this.allPlayer[i]) {
                this.allPlayer[i].clearByArenaOver();
            }
        }
        //this.bankerIndex = -1;
    }

    protected void savePrevInfo() {
        for (int i = 0; i < this.playerNum; ++i) {
            IRoomPlayer player = this.allPlayer[i];
            if (null == player) {
                continue;
            }
            player.savePrevInfo();
        }
    }

    @Override
    public void tick(long curTime, long delay) {
        tickAction(curTime, delay);
        tickHandle(curTime, delay);
    }
    
    private void tickHandle(long curTime, long delay) {
        getRoomHandle().tickHandle(curTime, delay);
    }
    
    private void tickAction(long curTime, long delay) {
        if (this.action.empty()) {
            return;
        }
        IAction a = this.action.peek();
        if (!a.canAction(curTime)) {
            return;
        }
        a = this.action.pop();
        if (!a.action(true)) {
            this.addAction(a);
        } else {
            if (!this.action.isEmpty()) {
                this.action.peek().recover();
            }
        }
    }
    

    @Override
    public void tick() {
        if (this.action.empty()) {
            return;
        }
        IAction a = this.action.pop();
        if (!a.action(false)) {
            this.addAction(a);
        } else {
            if (!this.action.isEmpty()) {
                this.action.peek().recover();
            }
        }
    }

    public void addAction(IAction action) {
        if (!this.action.isEmpty()) {
            this.action.peek().pause();
        }
        this.action.push(action);
    }

    /**
     * 记录
     */
    protected void record() {
        getRoomHandle().record();
    }

    /**
     * 保存房间分数
     */
    protected void saveRoomScore() {
        getRoomHandle().saveRoomScore();
    }

    public boolean isOwner(long playerUid) {
        return this.info.getOwnerPlayerUid() == playerUid;
    }
    
    @Override
    public long getOwnerPlayerUid() {
        return this.info.getOwnerPlayerUid();
    }

    @Override
    public ERoomState getRoomState() {
        return this.roomState.get();
    }

    @Override
    public boolean isNew() {
        return ERoomState.NEW == this.roomState.get();
    }

    @Override
    public boolean isStart() {
        return ERoomState.START == this.roomState.get();
    }

    @Override
    public boolean isFinish() {
        return ERoomState.FINISH == this.roomState.get();
    }

    @Override
    public boolean isDestroy() {
        return ERoomState.DESTROY == this.roomState.get();
    }

    @Override
    public boolean isAgain() {
        return ERoomState.AGAIN == this.roomState.get();
    }


    @Override
    public boolean isFull() {
        return this.playerNum <= this.playerCnt.get();
    }

    @Override
    public boolean isEmpty() {
        return 0 == this.playerCnt.get();
    }

    @Override
    public int getPlayerCnt() {
        return this.playerCnt.get();
    }

    @Override
    public boolean isWatchEmpty() {
        return 0 == this.watchList.size();
    }

    public int WatchSize() {
        return this.watchList.size();
    }

    @Override
    public boolean canWatch() {
        boolean isWatch = GameType.isWatchGame(this.getGameType());
        if (isWatch) {
            isWatch = this.playerMinNum < this.playerNum;
        }
        return isWatch;
    }

    @Override
    public int getMaxPlayerCnt() {
        return this.playerNum;
    }

    @Override
    public int getMinPlayerCnt() {
        return this.playerMinNum;
    }

    @Override
    public int getCurPlayerCnt() {
        return this.curPlayerCnt;
    }

    @Override
    public int getBankerIndex() {
        return this.bankerIndex;
    }

    @Override
    public IRoomPlayer getRoomPlayer(int index) {
        if (index < 0) {
            return null;
        }
        return this.allPlayer[index];
    }

    @Override
    public IRoomPlayer getNextRoomPlayer(int index) {
        IRoomPlayer player = null;
        int loop = 0;
        do {
            if (loop > this.playerNum) {
                return null;
            }
            player = this.allPlayer[(++index) % this.playerNum];
            ++loop;
        } while (null == player || player.isGuest() || player.isOver());
        return player;
    }

    @Override
    public IRoomPlayer getRoomPlayer(long playerUid) {
        try {
            for (int i = 0; i < this.playerNum; ++i) {
                IRoomPlayer player = this.allPlayer[i];
                if (null == player) {
                    continue;
                }
                if (player.getUid() == playerUid) {
                    return player;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }

    @Override
    public boolean isWatchPlayer(IRoomPlayer player) {
        return player.isGuest() || this.watchList.contains(player.getUid());
    }

    public boolean isWatchPlayer(long playerUid) {
        return this.watchList.contains(playerUid);
    }

    protected boolean isOperationTimeout(IRoomPlayer player) {
        return player.getOperationTimeoutCnt() >= 2;
    }

    protected String getFormatScore(int value) {
        return NumberUtils.get2Decimals(value);
    }

    protected int getScore(int value) {
        return this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1) * 100 * value;
    }

    protected int getDScore(int value) {
        return value / (this.info.getRule().getOrDefault(RoomRule.RR_END_POINT, 1) * 100);
    }

    protected String getClientScore(int value) {
        return this.getFormatScore(value);
    }

    @Override
    public void changeState(ERoomListState state) {
        if (ERoomType.NORMAL != this.roomType) {
            return;
        }
        IBoxOwner boxOwner = this.getBoxOwner();
        if (null == boxOwner) {
            return;
        }
        final Room self = this;
        AsyncTask.I.addTask(new Runnable() {
            @Override
            public void run() {
                PCLIRoomNtfChangeStateInfo info = new PCLIRoomNtfChangeStateInfo();
                info.groupUid = self.info.getGroupUid();
                info.roomId = self.getRoomId();
                if (ERoomListState.NEW == state) {
                    info.op = 1;
                    info.state = 1;
                } else if (ERoomListState.DEL == state) {
                    info.op = 2;
                    info.state = 2;
                } else if (ERoomListState.START == state) {
                    info.op = 4;
                    info.state = 4;
                } else {
                    info.op = 3;
                    if (ERoomListState.CAN_ADD == state) {
                        info.state = 1;
                    } else if (ERoomListState.FULL == state) {
                        info.state = 3;
                    }
                }
                if (ERoomListState.NEW == state) {
                    info.roomBriefInfo = self.getRoomBriefInfo();
                }
                ((IClub) boxOwner).broadcast(CommandId.CLI_NTF_ROOM_CHANGE_STATE, info);
            }
        });
    }

    public PCLIRoomNtfLocationRelationInfo getLocationRelationInfo() {
        PCLIRoomNtfLocationRelationInfo info = new PCLIRoomNtfLocationRelationInfo();
        try {
            for (int i = 0; i < this.playerNum; ++i) {
                IRoomPlayer player1 = this.allPlayer[i];
                if (null == player1) {
                    continue;
                }
                for (int j = i + 1; j < this.playerNum; ++j) {
                    IRoomPlayer player2 = this.allPlayer[j];
                    if (null == player2) {
                        continue;
                    }
                    info.location.add(new PCLIRoomNtfLocationRelationInfo.LocationRelation(player1.getUid()
                            , player2.getUid(), Location.getDistance(
                            player1.getLat(), player1.getLng(), player2.getLat(), player2.getLng())));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return info;
    }

    private void buildBoxRoomInfoByLhd(PCLIBoxRoomInfo roomInfo) {
        AbstractHundredRoomHandle roomHandle = (AbstractHundredRoomHandle) this.getRoomHandle();
        //add庄家，index = 0
        if (roomHandle.getCurBanker() != null) {
            IRoomPlayer player = this.getRoomPlayer(roomHandle.getCurBanker().getUid());
            if (null != player) {
                PCLIRoomPlayerInfo roomPlayerInfo = new PCLIRoomPlayerInfo();
                roomPlayerInfo.index = 0;
                roomPlayerInfo.playerInfo = player.getPlayer().getPlayerBriefInfo(null);
                roomInfo.players.add(roomPlayerInfo);
            }
        }
        //add座位玩家，index 1-8
        int num = 1;
        for (int i = 0; i < roomHandle.getVipSeats().length; i++) {
            //如果座位没人
            if (0 == roomHandle.getVipSeats()[i]) {
                continue;
            }
            //如果是庄家
            if (roomHandle.getCurBanker() != null && roomHandle.getVipSeats()[i] == roomHandle.getCurBanker().getUid()) {
                continue;
            }
            IRoomPlayer player = this.getRoomPlayer(roomHandle.getVipSeats()[i]);
            if (null != player) {
                PCLIRoomPlayerInfo roomPlayerInfo = new PCLIRoomPlayerInfo();
                roomPlayerInfo.index = num;
                num++;
                roomPlayerInfo.playerInfo = player.getPlayer().getPlayerBriefInfo(null);
                roomInfo.players.add(roomPlayerInfo);
            }
        }
        //add投注玩家
        for (int i = 0; i < roomHandle.getAllPlayerList().length; i++) {
            if (num > 8) {
                break;
            }
            long uid = roomHandle.getAllPlayerList()[i].getUid();
            //如果是庄家
            if (roomHandle.getCurBanker() != null && uid == roomHandle.getCurBanker().getUid()) {
                continue;
            }
            //如果是座位玩家
            boolean isIn = false;
            for (int j = 0; j < roomHandle.getVipSeats().length; j++) {
                if (uid == roomHandle.getVipSeats()[j]) {
                    isIn = true;
                    break;
                }
            }
            if (isIn) {
                continue;
            }
            IRoomPlayer player = this.getRoomPlayer(uid);
            if (null != player) {
                PCLIRoomPlayerInfo roomPlayerInfo = new PCLIRoomPlayerInfo();
                roomPlayerInfo.index = num;
                num++;
                roomPlayerInfo.playerInfo = player.getPlayer().getPlayerBriefInfo(null);
                roomInfo.players.add(roomPlayerInfo);
            }
        }
    }
    
    public PCLIBoxRoomInfo getBoxRoomInfo(Map<String, String> extra) {
        PCLIBoxRoomInfo roomInfo = new PCLIBoxRoomInfo();
        roomInfo.roomId = this.getRoomId();
        roomInfo.rule.putAll(this.info.getRule());
        roomInfo.curBureau = this.curBureau;
        roomInfo.gameType = this.info.getGameType();
        roomInfo.gameSubType = this.info.getGameSubType();
        roomInfo.extra.putAll(extra);
        roomInfo.remarks = this.info.getRemarks();
        try {
            if (this.getGameType() == GameType.GAME_TYPE_HUNDRED_LHD || this.getGameType() == GameType.GAME_TYPE_HUNDRED_BACCARAT) {
                buildBoxRoomInfoByLhd(roomInfo);
            } else {
                for (int i = 0; i < this.playerNum; ++i) {
                    IRoomPlayer player = this.allPlayer[i];
                    if (null != player) {
                        PCLIRoomPlayerInfo roomPlayerInfo = new PCLIRoomPlayerInfo();
                        roomPlayerInfo.playerInfo = player.getPlayer().getPlayerBriefInfo(null);
                        roomInfo.players.add(roomPlayerInfo);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roomInfo;
    }

    @Override
    public PCLIRoomInfo getRoomInfo() {
        PCLIRoomInfo roomInfo = new PCLIRoomInfo();
        if (this.roomType == ERoomType.BOX) {
            roomInfo.placeType = EPlaceType.GROUP.ordinal();
            roomInfo.placeUid = this.getGroupUid();
            roomInfo.remarks = this.info.getRemarks();
        } else {
            roomInfo.placeType = EPlaceType.HALL.ordinal();
        }
        roomInfo.ownerPlayerUid = this.info.getOwnerPlayerUid();
        roomInfo.roomId = this.getRoomId();
        roomInfo.roomBriefInfo = this.getRoomBriefInfo();
        if (this.getGameType() == GameType.GAME_TYPE_COW
                || this.getGameType() == GameType.GAME_TYPE_FRIED_GOLDEN_FLOWER
                || this.getGameType() == GameType.GAME_TYPE_THIRTEEN
                || this.getGameType() == GameType.GAME_TYPE_SG) {
            roomInfo.readyTime = ((IBoxRoomHandle)this.getRoomHandle()).getReadyTime();
        }
        ReentrantReadWriteLock.ReadLock readLock = this.rwLock.readLock();
        try {
            if (readLock.tryLock() || readLock.tryLock(1, TimeUnit.MINUTES)) {
                for (int i = 0; i < this.playerNum; ++i) {
                    IRoomPlayer player = this.allPlayer[i];
                    if (null != player) {
                        PCLIRoomPlayerInfo roomPlayerInfo = new PCLIRoomPlayerInfo();
                        roomPlayerInfo.playerInfo = player.getPlayer().getPlayerBriefInfo(null);
                        roomPlayerInfo.index = player.getIndex();
                        roomPlayerInfo.guess = player.isGuest();
                        roomPlayerInfo.state = this.ready.contains(player.getUid()) ? 1 : 0;
                        //roomPlayerInfo.score = this.getFormatScore(player.getScore());
                        roomPlayerInfo.score =(int)getPlayerGold(player)/100;
                        roomInfo.players.add(roomPlayerInfo);
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }
        return roomInfo;

    }
    
    protected PCLIRoomBriefInfo getRoomBriefInfo(IRoomPlayer roomPlayer) {
        PCLIRoomBriefInfo info = getRoomBriefInfo();
        info.curBureau = roomPlayer.getRoomPlayerHelper().getCurBureau();
        return info;
    }

    public PCLIRoomBriefInfo getRoomBriefInfo() {
        PCLIRoomBriefInfo roomInfo = new PCLIRoomBriefInfo();
        roomInfo.roomId = this.getRoomId();
        roomInfo.gameType = this.info.getGameType();
        roomInfo.gameSubType = this.info.getGameSubType();
        roomInfo.roomType = this.roomType.ordinal();
        roomInfo.rule.putAll(this.info.getRule());
        roomInfo.curBureau = this.curBureau;
        roomInfo.gameing = this.roomState.get() == ERoomState.START;
        roomInfo.timeout = this.timeout > 0 ? this.timeout : Constant.ROOM_TAKE_TIMEOUT;
        roomInfo.ownerType = this.info.ownerType;
        if (ERoomState.NEW == this.roomState.get()) {
            // 可加入
            if (this.playerNum == this.playerCnt.get()) {
                roomInfo.state = 3;
            } else {
                roomInfo.state = 1;
            }
        } else if (ERoomState.FINISH == this.roomState.get()) {
            // 已经结束
            roomInfo.state = 2;
        } else {
            // 已经开始
            roomInfo.state = 4;
        }
        return roomInfo;
    }

    @Override
    public void broadcast2Client(int commandId, Object msg) {
        this.broadcast2Client(commandId, msg, true);
    }

    @Override
    public void broadcast2Client(int commandId, Object msg, boolean syncWatch) {
        ReentrantReadWriteLock.ReadLock readLock = this.rwLock.readLock();
        try {
            if (readLock.tryLock() || readLock.tryLock(1, TimeUnit.MINUTES)) {
                for (int i = 0, len = this.allPlayer.length; i < len; ++i) {
                    IRoomPlayer playerInfo = this.allPlayer[i];
                    if (null == playerInfo) {
                        continue;
                    }
                    if (playerInfo.isGuest()) {
                        continue;
                    }
                    if (playerInfo.isOffline() 
                    		&&(playerInfo.getUid()<60000000
                    		|| playerInfo.getUid()>=70000000)
                    		&& commandId==CommandId.CLI_NTF_ROOM_GAMEOVER) {
                        continue;
                    }
                    //playerInfo.send(commandId, msg);
                    Player player = PlayerManager.I.getOnlinePlayer(playerInfo.getUid());
                    if (null != player && player.isOnline() && player.getRoomId() == this.getRoomId()) {
                        player.send(commandId, msg);
                    }else if(null == player 
                    		&& commandId==CommandId.CLI_NTF_ROOM_GAMEOVER) 
                    {
                    	
                    	// 设置执行时间
                		Calendar calendar1 = Calendar.getInstance();
                		int year1 = calendar1.get(Calendar.YEAR);
                		int month1 = calendar1.get(Calendar.MONTH);
                		int day1 = calendar1.get(Calendar.DAY_OF_MONTH);// 每天
                		int hour1 = calendar1.get(Calendar.HOUR_OF_DAY);
                		int minute1 = calendar1.get(Calendar.MINUTE);
                		int second1 = calendar1.get(Calendar.SECOND);
                		// 定制每天的执行时间
                		calendar1.set(year1, month1, day1, hour1, minute1, second1+8);
                		Date date1 = calendar1.getTime();
                		Timer timer1 = new Timer();
                		//定时开启
                		TimerTask task1 = new TimerTask() {
                			public void run() {
                				Player pl = PlayerManager.I.getPlayer(playerInfo.getUid());
                            	RoomManager.I.leave(pl);
                			}
                		};
                		//执行一次
                		timer1.schedule(task1, date1);
                    }
                }
            } else {
                Logs.ROOM.warn("roomId:"+this.getRoomId() + ",roomUid:"+this.getRoomUid()+",commandId:"+commandId+" 尝试获取读锁時沒有拿到！！！！");
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }

        if (syncWatch) {
            Iterator<Long> it = this.watchList.iterator();
            while (it.hasNext()) {
                Player player = PlayerManager.I.getOnlinePlayer(it.next());
                if (null != player && player.isOnline() && player.getRoomId() == this.getRoomId()) {
                    player.send(commandId, msg);
                }
            }
        }
    }

    public void broadcast2Client(int commandId, Object msg, long playerUid) {
        this.broadcast2Client(commandId, msg, playerUid, true);
    }

    public void broadcast2Client(int commandId, Object msg, long playerUid, boolean syncWatch) {
        ReentrantReadWriteLock.ReadLock readLock = this.rwLock.readLock();
        try {
            if (readLock.tryLock() || readLock.tryLock(1, TimeUnit.MINUTES)) {
                for (int i = 0, len = this.allPlayer.length; i < len; ++i) {
                    IRoomPlayer playerInfo = this.allPlayer[i];
                    if (null == playerInfo) {
                        continue;
                    }
                    if (playerInfo.getUid() == playerUid) {
                        continue;
                    }
                    if (playerInfo.isGuest()) {
                        continue;
                    }
                    if (playerInfo.isOffline()) {
                        continue;
                    }
                    //playerInfo.send(commandId, msg);
                    Player player = PlayerManager.I.getOnlinePlayer(playerInfo.getUid());
                    if (null != player && player.isOnline() && player.getRoomId() == this.getRoomId()) {
                        player.send(commandId, msg);
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }

        if (syncWatch) {
            Iterator<Long> it = this.watchList.iterator();
            while (it.hasNext()) {
                Player player = PlayerManager.I.getOnlinePlayer(it.next());
                if (null != player && player.getUid() != playerUid && player.getRoomId() == this.getRoomId()) {
                    player.send(commandId, msg);
                }
            }
        }
    }

    public void broadcast2ClientWithWatch(int commandId, Object msg) {
        ReentrantReadWriteLock.ReadLock readLock = this.rwLock.readLock();
        try {
            if (readLock.tryLock() || readLock.tryLock(1, TimeUnit.MINUTES)) {
                for (int i = 0, len = this.allPlayer.length; i < len; ++i) {
                    IRoomPlayer playerInfo = this.allPlayer[i];
                    if (null == playerInfo) {
                        continue;
                    }
                    if (!playerInfo.isGuest()) {
                        continue;
                    }
                    if (playerInfo.isOffline()) {
                        continue;
                    }
                  //playerInfo.send(commandId, msg);
                    Player player = PlayerManager.I.getOnlinePlayer(playerInfo.getUid());
                    if (null != player && player.isOnline() && player.getRoomId() == this.getRoomId()) {
                        player.send(commandId, msg);
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }

        Iterator<Long> it = this.watchList.iterator();
        while (it.hasNext()) {
            Player player = PlayerManager.I.getOnlinePlayer(it.next());
            if (null != player && player.isOnline() && player.getRoomId() == this.getRoomId()) {
                player.send(commandId, msg);
            }
        }
    }

    @Override
    public boolean save() {
        if (!this.info.isDirty()) {
            return false;
        }
        this.info.setDirty(false);
        final Room self = this;
        DBManager.I.save(new Task() {
            @Override
            public void run() {
                if (!DBManager.I.getRoomDao().save(self.info)) {
                    self.info.setDirty(true);
                    Logs.PLAYER.warn("%s 保存数据库失败", self.info);
                }
            }
        });
        return true;
    }

    public void setIsDestroyUid(long isDestroyUid) {
        this.isDestroyUid = isDestroyUid;
    }

    public void setDestroyType(ERoomDestroyType type) {
        this.destroyType = type;
    }

    @Override
    public boolean checkIsDestroy() {
        return this.destroyType.ordinal() != ERoomDestroyType.UN_DESTROY.ordinal();
    }

    @Override
    public IRoomScore getRoomScore() {
        return this.roomScore;
    }

    @Override
    public Record getRecord() {
        return this.record;
    }

    @Override
    public long getGroupUid() {
        return this.info.getGroupUid();
    }

    @Override
    public ERoomType getRoomType() {
        return this.roomType;
    }

    @Override
    public int getGameType() {
        return this.info.getGameType();
    }

    @Override
    public int getGameSubType() {
        return this.info.getGameSubType();
    }

    @Override
    public HashMap<String, Integer> getRule() {
        return this.info.getRule();
    }

    @Override
    public long getRoomUid() {
        return this.info.getUid();
    }

    @Override
    public int getRoomId() {
        return this.info.getRoomId();
    }

    @Override
    public int getBureau() {
        return this.bureau;
    }

    @Override
    public int getCurBureau() {
        return this.curBureau;
    }

    public LinkedList<Byte> getAllCard() {
        return allCard;
    }

    @Override
    public boolean getDetectionIP() {
        return this.detectionIP;
    }

    @Override
    public IBoxOwner getBoxOwner() {
        return ClubManager.I.getClubByUid(this.getGroupUid());
    }

    @Override
    public List<IRoomPlayer> getCurrPlayers() {
        List<IRoomPlayer> list = new ArrayList<>();
        for (IRoomPlayer player : allPlayer) {
            if (player == null || player.isGuest() || player.isOver()) {
                continue;
            }
            list.add(player);
        }
        return list;
    }

    @Override
    public List<Long> getCurrPlayerIds() {
        List<Long> list = new ArrayList<>();
        for (IRoomPlayer player : allPlayer) {
            if (player == null || player.isGuest() || player.isOver()) {
                continue;
            }
            list.add(player.getUid());
        }
        return list;
    }

    @Override
    public String toString() {
        return String.format("Room[RoomUid:%d, RoomId:%d State:%s Bureau:%d/%d WatchCnt:%d Rule:%s AllPlayer:%s]",
                this.getRoomUid(), this.getRoomId(), this.roomState.get(), this.curBureau, this.bureau,
                this.watchList.size(), this.info.getRule(), Arrays.toString(this.allPlayer));
    }

    @Override
    public boolean isReady(long playerUid) {
        return this.ready.contains(playerUid);
    }

    @Override
    public Integer getTemporaryPropertyValue(long playerUid, String propertyType) {
        if (temporaryPropertyMap.containsKey(playerUid)) {
            Map<String, Integer> tempMap = temporaryPropertyMap.get(playerUid);
            return tempMap.containsKey(propertyType) ? tempMap.get(propertyType) : 0;
        }
        return 0;
    }

    @Override
    public void setTemporaryPropertyValue(long playerUid, String propertyType, Integer value) {
        Map<String, Integer> tempMap = null;
        if (temporaryPropertyMap.containsKey(playerUid)) {
            tempMap = temporaryPropertyMap.get(playerUid);
        } else {
            tempMap = new HashMap<>();
            temporaryPropertyMap.put(playerUid, tempMap);
        }
        if (tempMap.containsKey(propertyType)) {
            tempMap.put(propertyType, tempMap.get(propertyType) + value);
        } else {
            tempMap.put(propertyType, value);
        }
    }

    @Override
    public long getFromClubUid(long playerUid) {
        return getRoomHandle().getFromClubUid(playerUid);
    }

    public List<HelperInfo> getHelperInfo() {
        List<HelperInfo> tempList = new ArrayList<>();
        ReentrantReadWriteLock.ReadLock readLock = this.rwLock.readLock();
        try {
            if (readLock.tryLock() || readLock.tryLock(1, TimeUnit.MINUTES)) {
                for (int i = 0; i < this.playerNum; ++i) {
                    IRoomPlayer player = this.allPlayer[i];
                    if (null != player && !player.isGuest()) {
                        HelperInfo info = new HelperInfo();
                        info.name = player.getPlayer().getName();
                        info.state = this.roomState.get() == ERoomState.START ? 2 : this.ready.contains(player.getUid()) ? 1 : 0;
                        tempList.add(info);
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }
        return tempList;
    }

    @Override
    public Set<Long> getGuestPlayerUids() {
        Set<Long> uids = new HashSet<>();
        for (int i = 0; i < this.playerNum; ++i) {
            IRoomPlayer player = this.allPlayer[i];
            if (null != player && player.isGuest()) {
                uids.add(player.getUid());
            }
        }
        return uids;
    }

    private void readyCountDown() {
        if (!this.autoStart && this.autoReady) {
            ReadyCountDownAction action = new ReadyCountDownAction(this, this.autoStartTime * 3);
            for (int i = 0; i < this.playerNum; ++i) {
                IRoomPlayer player = this.allPlayer[i];
                if (null == player || player.isGuest()) {
                    continue;
                }
                action.addReadyPlayer(player.getUid());
            }
            this.addAction(action);
        }
    }

    private void sendReadyCountDown(Player player) {
        if (!this.action.isEmpty()) {
            IAction action = this.action.peek();
            if (action instanceof ReadyCountDownAction) {
                ((ReadyCountDownAction) action).selectReadyPlayer(player.getUid(), 0);
            }
        }
    }

    public long getPlayerGold(IRoomPlayer roomPlayer){
        long clubUid=getFromClubUid(roomPlayer.getUid());
        if(clubUid!=-1){
            IClub club= ClubManager.I.getClubByUid(clubUid);
            if(club!=null) {
                return club.getMemberExt(roomPlayer.getUid(), true).getGold();
            }
        }
        return 0;
    }

    public long getPlayerGold(long playerUid){
        long clubUid=getFromClubUid(playerUid);
        if(clubUid!=-1){
            IClub club= ClubManager.I.getClubByUid(clubUid);
            if(club!=null) {
                return club.getMemberExt(playerUid, true).getGold();
            }
        }
        return 0;
    }
    
    @Override
    public IRoomHandle getRoomHandle() {
        return this.roomHandle;
    }
    
    @Override
    public void setRoomHandle(IRoomHandle roomHandle) {
        this.roomHandle = roomHandle;
    }
    
    @Override
    public int getPlayerNum() {
        return this.playerNum;
    }
    
    @Override
    public ReentrantReadWriteLock getLock() {
        return this.rwLock;
    }
    
    @Override
    public IRoomPlayer[] getAllPlayer() {
        return this.allPlayer;
    }
    
    @Override
    public int addPlayerCnt() {
         return playerCnt.incrementAndGet();
    }
    
    @Override
    public int getAndDecrPlayerCnt() {
         return playerCnt.getAndDecrement();
    }
    
    @Override
    public void addWatchPlayerUid(long uid) {
        watchList.add(uid);
    }
    
    @Override
    public boolean removeWatch(long uid) {
        return this.watchList.remove(uid);
    }
    
    @Override
    public boolean removeReady(long uid) {
        return this.ready.remove(uid);
    }
    
    @Override
    public int getPlayerMinNum() {
        return this.playerMinNum;
    }
    
    @Override
    public int getReadySize() {
        return this.ready.size();
    }
    
    @Override
    public int getCost() {
        return this.info.getCost();
    }
    
    @Override
    public int getFinishBureauCount() {
        return this.finishBureauCount;
    }
    
    @Override
    public void setRoomType(ERoomType roomType) {
        this.roomType = roomType;
    }

    @Override
    public Stack<IAction> getAction(){
        return this.action;
    }
    
    @Override
    public IRoomPlayer createPlayer() {
        return getRoomHandle().createPlayer();
    }

    /**
     * 好牌处理
     */
    protected void doGoodPoker() {
    	try {
    		Map<Integer,LinkedList<Byte>> playerGoodCards = getPlayerGoodCards();
    		if (playerGoodCards != null && !playerGoodCards.isEmpty()) {
        		doDealGoodCards(playerGoodCards);
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * 获取玩家好牌
     * @return
     */
    private Map<Integer,LinkedList<Byte>> getPlayerGoodCards() {
    	if (this.bankerIndex == -1) {
    		return null;
    	}
    	List<IRoomPlayer> tempPlayers = getContinueLostPlayers();
    	if (null == tempPlayers || tempPlayers.size() == 0) {
    		return null;
    	}

    	/** 每局玩家好牌 */
        Map<Integer, LinkedList<Byte>> playerGoodCards = new HashMap<Integer, LinkedList<Byte>>();
    	// 类型 1：一个好牌率玩家2两个好牌率玩家
    	int playType = getGoodPokerPlayType(tempPlayers);
    	if (playType == 1) {
    		GoodPokerInfo info = randomGoodPokerInfo(getGameType(), playType);
    		if (null == info || info.getAllCard() == null) {
    			return null;
    		}
    		List<LinkedList<Byte>> tempList = info.getAllCard();
    		if (tempList.size() != 1) {
    			return null;
    		}
    		LinkedList<Byte> tempCards = new LinkedList<Byte>();
    		tempCards.addAll(tempList.get(0));
    		playerGoodCards.put(tempPlayers.get(0).getIndex(), tempCards);
    	} else if (playType == 2) {
    		GoodPokerInfo info = randomGoodPokerInfo(getGameType(), playType);
    		if (null == info || info.getAllCard() == null) {
    			return null;
    		}
    		List<LinkedList<Byte>> tempList = info.getAllCard();
    		if (tempList.size() != 2) {
    			return null;
    		}
    		LinkedList<Byte> tempCards = new LinkedList<Byte>();
    		tempCards.addAll(tempList.get(0));
    		playerGoodCards.put(tempPlayers.get(0).getIndex(), tempCards);
    		tempCards = new LinkedList<Byte>();
    		tempCards.addAll(tempList.get(1));
    		playerGoodCards.put(tempPlayers.get(1).getIndex(), tempCards);
    	}
    	return playerGoodCards;
    }
    
    protected abstract void doDealGoodCards(Map<Integer,LinkedList<Byte>> playerGoodCards); 
    
    /**
	 * 随机一手好牌
	 * @param gameType
	 * @param playType
	 * @return
	 */
	protected GoodPokerInfo randomGoodPokerInfo(int gameType, int playType) {
		if (this.goodPokerMap.containsKey(playType)) {
			List<GoodPokerInfo> tempList = this.goodPokerMap.get(playType);
			if (null == tempList || tempList.size() == 0) {
				List<GoodPokerInfo> sourceList = GoodPokerManager.I.getGoodPokerInfo(gameType, playType);
				if (sourceList != null) {
					tempList = new ArrayList<>();
					tempList.addAll(sourceList);
					this.goodPokerMap.put(playType, tempList);
				}
			}
			if (null == tempList || tempList.size() == 0) {
				return null;
			}
			int index = RandomUtil.random(0, tempList.size() - 1);
			GoodPokerInfo info = tempList.remove(index);
			return info;
		}
		return null;
	}
    
    @Override
    public ERoomDestroyType getRoomDestoryType() {
        return destroyType;
    }
    
    @Override
    public long getDestroyUid() {
        return isDestroyUid;
    }
    
    protected List<IRoomPlayer> getContinueLostPlayers() {
        List<IRoomPlayer> tempPlayers = null;
        int tempCount = playerNum == 2 ? 3 : 4;
        for (int j = 0, len = this.allPlayer.length; j < len; ++j) {
            IRoomPlayer player = this.allPlayer[j];
            if (null == player || player.isGuest()) {
                continue;
            }
            // 获取连续输的次数
            int lostCount = player.getScore(Score.ACC_LOST_CNT_CONTINUE, true);
            if (lostCount >= tempCount) {
                if (null == tempPlayers) {
                    tempPlayers = new ArrayList<IRoomPlayer>();
                }
                tempPlayers.add(player);
            }
        }
        return tempPlayers;
    }
    
    protected int getGoodPokerPlayType(List<IRoomPlayer> tempPlayers) {
    	return tempPlayers.size() == 1 ? 1 : 2;
    }
    
    public Set<Long> getWatchList() {
        return this.watchList;
    }
    
    public JSONObject getPlayerPokerCardInfo(CowPlayer player) {
    	//排序
        PokerUtil.sortByCow(player.getHandCard());
        List<Byte> result = new ArrayList<>(5);
        EPokerCardType cardType = EPokerCardType.COW_NONE;
        double cardValue = -1;
        do {
            cardValue = PokerUtil.isCowWithTheFlower(player.getHandCard(), result);
            if (-1 != cardValue) {
                cardType = EPokerCardType.COW_WITH_THE_FLOWER;
                break;
            }
            cardValue = PokerUtil.isCowDragon(player.getHandCard(), result);
            if (-1 != cardValue) {
                cardType = EPokerCardType.COW_DRAGON;
                break;
            }
            cardValue = PokerUtil.isCowBomb(player.getHandCard(), result);
            if (-1 != cardValue) {
                cardType = EPokerCardType.COW_BOMB;
                break;
            }
            cardValue = PokerUtil.isCowFiveSmall(player.getHandCard(), result);
            if (-1 != cardValue) {
                cardType = EPokerCardType.COW_FIVE_SMALL;
                break;
            }
            cardValue = PokerUtil.isCowCucurbit(player.getHandCard(), result);
            if (-1 != cardValue) {
                cardType = EPokerCardType.COW_CUCURBIT;
                break;
            }
            cardValue = PokerUtil.isCowGold(player.getHandCard(), result);
            if (-1 != cardValue) {
                cardType = EPokerCardType.COW_GOLD;
                break;
            }
            cardValue = PokerUtil.isCowSameColor(player.getHandCard(), result);
            if (-1 != cardValue) {
                cardType = EPokerCardType.COW_SAME_COLOR;
                break;
            }
            cardValue = PokerUtil.isCowSilver(player.getHandCard(), result);
            if (-1 != cardValue) {
                cardType = EPokerCardType.COW_SILVER;
                break;
            }
            cardValue = PokerUtil.isCowStraight(player.getHandCard(), result);
            if (-1 != cardValue) {
                cardType = EPokerCardType.COW_STRAIGHT;
                break;
            }
        } while (false);

        if (cardType == EPokerCardType.COW_NONE) {
            cardType = PokerUtil.findCow(player.getHandCard(), result);
            byte value = player.getHandCard().get(4);
            if (cardType == EPokerCardType.COW_NONE) {
                cardValue = PokerUtil.generateCardValueByCow2WithColor(PokerUtil.getCardValueByCow2(value),
                        (byte) (PokerUtil.getCardColor(value) + 1)) * Math.pow(128, 0);
            } else {
                cardValue = PokerUtil.generateCardValueByCow2WithColor(PokerUtil.getCardValueByCow2(value),
                        (byte) (PokerUtil.getCardColor(value) + 1)) * Math.pow(128, 1);
            }
        }
        JSONObject json = new JSONObject();
        json.put("cardType", cardType);
        json.put("cardValue", cardValue);
    	return json;
    }
    
}
