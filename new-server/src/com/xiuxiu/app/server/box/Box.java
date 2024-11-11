package com.xiuxiu.app.server.box;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.box.PCLIBoxInfo;
import com.xiuxiu.app.protocol.client.box.PCLIBoxNtfChangeStateInfo;
import com.xiuxiu.app.protocol.client.box.PCLIBoxNtfJoinInfo;
import com.xiuxiu.app.protocol.client.box.PCLIBoxRoomStateInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Main;
import com.xiuxiu.app.server.box.constant.EBoxState;
import com.xiuxiu.app.server.box.constant.EBoxType;
import com.xiuxiu.app.server.box.handle.BoxHandle;
import com.xiuxiu.app.server.box.handle.IBoxHandle;
import com.xiuxiu.app.server.box.handle.WatchBoxHandle;
import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.app.server.forbid.ForbidManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.ERoomDestroyType;
import com.xiuxiu.app.server.room.ERoomState;
import com.xiuxiu.app.server.room.ERoomType;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.RoomRule;
import com.xiuxiu.app.server.room.handle.IBoxRoomHandle;
import com.xiuxiu.app.server.room.handle.IRoomHandle;
import com.xiuxiu.app.server.room.handle.RoomHandleFactory;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.queue.AsynchronousQueueLock;
import com.xiuxiu.core.queue.FixedAsynchronousQueue;
import com.xiuxiu.core.thread.BaseThread;
import com.xiuxiu.core.thread.Tick;
import com.xiuxiu.core.utils.JsonUtil;

/**
 * 包厢实体类
 * 
 * @author Administrator
 *
 */
public class Box extends BaseTable implements Tick {
    /** 包厢类型, 0: 固定模式, 1: 自定义 */
    private int boxType;
    /** 包厢名称 */
    private String boxName;
    /** 归属uid */
    private long ownerUid;
    /** 楼层uid */
    private long floorUid = -1;
    /** 游戏类型 */
    private int gameType;
    /** 游戏子类型 */
    private int gameSubType;
    /** 规则 */
    private HashMap<String, Integer> rule = new HashMap<>();
    /** 额外参数 */
    private HashMap<String, String> extra = new HashMap<>();
    /** 包厢创建时间 */
    private long createTime;
    /** 归属类型 */
    private int ownerType;

    /** 是否待关闭 */
    private boolean waitClose = false;

    private transient ConcurrentHashMap<Long, Player> allWatchPlayer = new ConcurrentHashMap<>();

    private transient ConcurrentHashMap<Long, IRoomPlayer> allPlayer = new ConcurrentHashMap<>();
    /** 每个玩法桌最多可以创建n个游戏桌 */
    private transient FixedAsynchronousQueue<IBoxRoomHandle> allRoomHandle = null;
    private transient AtomicReference<EBoxState> state = new AtomicReference<>(EBoxState.INIT);
    private transient ConcurrentHashMap<Long, IBoxHandle> allBoxHandles = new ConcurrentHashMap<>();

    private transient BaseThread currThread;
    protected long closeTime;
    
    public Box() {
        this.setTableType(ETableType.TB_BOX);
    }

    public void init() {
        EBoxType boxType = EBoxType.getType(getBoxType());
        this.allRoomHandle = new FixedAsynchronousQueue<IBoxRoomHandle>(boxType.getMaxRoomSize());
        this.currThread = Main.I.getRoomMessageProcess().getThread(this.uid,boxType == EBoxType.HUNDRED);
        this.currThread.attackTick(this);
    }
    
    @Override
    public void tick(long curTime, long delay) {
        if (EBoxState.INIT == this.state.get()) {
            //if (this.closeTime <= curTime) {
            //    // 时间到关闭
            //    this.close();
            //    return;
            //}
        } else if (EBoxState.WAIT_CLOSE == this.state.get()) {
            this.checkClose();
            return;
        }
        if (this.isClose()) {
            return;
        }
    }
    
    public boolean isClose() {
        return EBoxState.INIT != this.state.get();
    }
    
    private ErrorCode checkJoinBoxRoom(Player player, IRoom room) {
        Room roomInfo = (Room) RoomManager.I.getRoom(room.getRoomId());
        ErrorCode ec = RoomManager.I.checkIpSame(player, roomInfo);
        if (ec != ErrorCode.OK) {
            return ec;
        }
        ec = ForbidManager.I.isForbid(this.getOwnerType(), this.ownerUid, player.getUid(),
                room.getCurrPlayerIds());
        if (ec != ErrorCode.OK) {
            return ec;
        }
        return ErrorCode.OK;
    }
    
    private IBoxRoomHandle fastJoinBoxRoom(Player player) {
        AsynchronousQueueLock<IBoxRoomHandle>[] fixedAsynchronousSource = this.allRoomHandle.getSource();
        for (int i = 0, len = fixedAsynchronousSource.length; i < len; ++i) {
            AsynchronousQueueLock<IBoxRoomHandle> asynchronousQueueLock = fixedAsynchronousSource[i];
            ReentrantLock spaceLock = asynchronousQueueLock.getSpaceLock();
            try {
                // 得到锁
                spaceLock.lock();
                if (asynchronousQueueLock.get() != null) {
                    IBoxRoomHandle boxRoomHandle = asynchronousQueueLock.get();
                    IRoom room = boxRoomHandle.getRoom();
                    //金花牛牛
                    if (GameType.isArenaGame(this.getGameType())) {
                    //if (this.getGameType() == GameType.GAME_TYPE_COW || this.getGameType() == GameType.GAME_TYPE_FRIED_GOLDEN_FLOWER|| this.getGameType() == GameType.GAME_TYPE_THIRTEEN) {
                        if (room.getPlayerCnt() >= room.getPlayerNum()) {
                            continue;
                        }
                    } else if (ERoomState.NEW != room.getRoomState() && ERoomState.AGAIN != room.getRoomState() && ERoomState.AUTO_START != room.getRoomState()) {
                        continue;
                    }
                    ErrorCode errorCode = checkJoinBoxRoom(player, room);
                    if (errorCode != ErrorCode.OK) {
                        continue;
                    }
                    return boxRoomHandle;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (spaceLock.isHeldByCurrentThread()) {
                    spaceLock.unlock();
                }
            }
        }
        return null;
    }

    public IBoxRoomHandle createBoxRoom(Player player) {
        // 创建房间
        IRoom room = (IRoom) RoomManager.I.createBoxRoom(player, this, gameType, gameSubType, rule, null);
        if (null == room) {
            Logs.GROUP.warn("%s创建房间失败%d", player.getUid(),gameType);
            return null;
        }
        room.setRoomType(ERoomType.BOX);
        // 创建房间业务逻辑处理器
        room.setRoomHandle(RoomHandleFactory.createRoomHandle(room, this));
        IBoxRoomHandle boxRoomHandle = (IBoxRoomHandle) room.getRoomHandle();
        allRoomHandle.register(boxRoomHandle);
        if (boxRoomHandle.getIndex() == -1) {
            return null;
        }
        this.onCreateRoom(room);
        return boxRoomHandle;
    }
    
    public ErrorCode join(Player player, int roomIndex) {
        if (EBoxState.INIT != this.state.get()) {
            Logs.GROUP.warn("%s 正处于关闭/关闭中, 无法加入", this);
            return ErrorCode.GROUP_BOX_CLOSE;
        }
        IBoxRoomHandle boxRoomHandle = null;
        try {
            if (-1 == roomIndex) {
                // 判断是否创建自定义房间
                if (EBoxType.CUSTOM.match(this.boxType)) {
                    Logs.GROUP.warn("自定义包厢, 房间索引;%d创建房间错误", roomIndex);
                    return ErrorCode.REQUEST_INVALID;
                } else if (EBoxType.HUNDRED.match(this.boxType)) {
                    return ErrorCode.REQUEST_INVALID;
                }
                boxRoomHandle = createBoxRoom(player);
                if (null == boxRoomHandle) {
                    return ErrorCode.BOX_ROOM_NOT_EXIST;
                }
            } else if (-2 == roomIndex) {
                // 快速开始
                if (EBoxType.CUSTOM.match(this.boxType)) {
                    return ErrorCode.REQUEST_INVALID;
                } else if (EBoxType.HUNDRED.match(this.boxType)) {
                    return ErrorCode.REQUEST_INVALID;
                }
                boxRoomHandle = fastJoinBoxRoom(player);
                if (null == boxRoomHandle) {
                    player.send(CommandId.CLI_NTF_BOX_JOIN_FAST_FAIL, null);
                    return null;
                }
            } else if (roomIndex < 0 || roomIndex >= EBoxType.getType(getBoxType()).getMaxRoomSize()) {
                return ErrorCode.REQUEST_INVALID_DATA;
            } else {
                AsynchronousQueueLock<IBoxRoomHandle> queueLock = allRoomHandle.getSource()[roomIndex];
                boxRoomHandle = queueLock.get();
                if (null == boxRoomHandle) {
                    if (EBoxType.CUSTOM.match(this.boxType)) {
                        Logs.GROUP.warn("自定义包厢, 房间索引;%d还未创建房间", roomIndex);
                        return ErrorCode.REQUEST_INVALID;
                    }
                    return ErrorCode.BOX_ROOM_NOT_EXIST;
                }
                Room roomInfo = (Room) RoomManager.I.getRoom(boxRoomHandle.getRoomId());
                ErrorCode ec = RoomManager.I.checkIpSame(player, roomInfo);
                //百人场不检测IP
                if (!EBoxType.HUNDRED.match(this.boxType)) {
                    if (ec != ErrorCode.OK) {
                        return ec;
                    }
                }
                //竞技场牛牛不检测IP
                if (boxRoomHandle.getRoom().getGameType() == GameType.GAME_TYPE_COW) {
                    if (!EBoxType.ARENA.match(this.boxType)) {
                        if (ec != ErrorCode.OK) {
                            return ec;
                        }
                    }
                }
                ec = ForbidManager.I.isForbid(this.getOwnerType(), this.ownerUid, player.getUid(),
                        boxRoomHandle.getRoom().getCurrPlayerIds());
                if (ec != ErrorCode.OK) {
                    return ec;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (null == boxRoomHandle) {
            return ErrorCode.REQUEST_INVALID;
        }

        IRoom room = boxRoomHandle.getRoom();
        IRoomPlayer roomPlayer = this.allPlayer.get(player.getUid());
        if (room.getGameType() == GameType.GAME_TYPE_HUNDRED_LHD || room.getGameType() == GameType.GAME_TYPE_HUNDRED_BACCARAT) {
            if (null == roomPlayer) {
                roomPlayer = room.createPlayer();
                roomPlayer.setPlayer(player);
                this.allPlayer.put(player.getUid(), roomPlayer);
                roomPlayer = this.allPlayer.get(player.getUid());
            }
        } else {
            //if (null == roomPlayer) {
            roomPlayer = room.createPlayer();
            roomPlayer.setPlayer(player);
            this.allPlayer.put(player.getUid(), roomPlayer);
            roomPlayer = this.allPlayer.get(player.getUid());
            //}
        }

        ErrorCode err = null;
        if (!EBoxType.HUNDRED.match(this.boxType) && BoxManager.I.isWatch(room.getGameType(), room.getRule())) {
            IBoxHandle handle = allBoxHandles.get(room.getRoomUid());
            if (null == handle) {
                return ErrorCode.ROOM_NOT_IN;
            }
            err = handle.onJoin(roomPlayer);
        } else {
            err = boxRoomHandle.join(roomPlayer);
        }
        if (ErrorCode.OK == err) {
            if (!EBoxType.HUNDRED.match(this.boxType)) {
                roomPlayer.setRoom(room);
                noticeJoin(player, room, boxRoomHandle.getIndex());
                if (BoxManager.I.isWatch(room.getGameType(), room.getRule()) && (room.getRoomState() == ERoomState.START || (room.isFull()
                        && room.getRoomState() != ERoomState.FINISH && room.getRoomState() != ERoomState.DESTROY))) {
                    room.watchPlayerSyncDeskInfo((IRoomPlayer) roomPlayer);
                }
            }
        } else {
            this.allPlayer.remove(roomPlayer.getUid());
        }
        return err;
    }
    
    private void noticeJoin(Player player, IRoom room, int index) {
        PCLIBoxNtfJoinInfo joinInfo = new PCLIBoxNtfJoinInfo();
        joinInfo.boxUid = this.uid;
        joinInfo.groupUid = this.ownerUid;
        joinInfo.gameType = room.getGameType();
        joinInfo.gameSubType = room.getGameSubType();
        joinInfo.roomIndex = index;
        joinInfo.allPlayerCnt = this.allPlayer.size();
        joinInfo.playerBriefInfo = player.getPlayerBriefInfo(null);
        room.broadcast2Client(CommandId.CLI_NTF_ROOM_INFO, ((Room) room).getRoomInfo());
        player.send(CommandId.CLI_NTF_BOX_JOIN, joinInfo);
    }

    private void onCreateRoom(IRoom room) {
        if (BoxManager.I.isWatch(room.getGameType(), room.getRule())) {
            allBoxHandles.put(room.getRoomUid(), new WatchBoxHandle(this, room));
        } else {
            allBoxHandles.put(room.getRoomUid(), new BoxHandle(this, room));
        }
        RoomManager.I.addBoxRoom(room, this);
    }

    public void join(IRoomPlayer player) {
        this.allPlayer.put(player.getUid(), player);
    }
    
    public void leave(long playerUid, int roomId) {
        this.allPlayer.remove(playerUid);
        this.allWatchPlayer.remove(playerUid);
        IRoom room = RoomManager.I.getRoom(roomId);
        if (room != null) {
            if (room.getRoomState() == ERoomState.NEW && room.isEmpty() && room.isWatchEmpty()) {
                this.allRoomHandle.remove((IBoxRoomHandle) room.getRoomHandle());
                IBoxHandle handle = allBoxHandles.remove(room.getRoomUid());
                if (handle != null) {
                    handle.killAll(this);
                }
                RoomManager.I.removeRoom((Room) room);
            } else {
                IBoxHandle handle = allBoxHandles.get(room.getRoomUid());
                if (handle != null) {
                    handle.level(playerUid);
                }
            }
        }
    }

    public void killAll(int roomIndex, long roomUid, List<Long> killPlayerUids) {
        if (killPlayerUids != null) {
            for (long playerUid : killPlayerUids) {
                this.allPlayer.remove(playerUid);
                this.allWatchPlayer.remove(playerUid);
            }
        }
        IBoxHandle handle = allBoxHandles.remove(roomUid);
        if (handle != null) {
            handle.killAll(this);
        }
        if (roomIndex != -1) {
            IBoxRoomHandle boxRoomHandle = this.allRoomHandle.getSource()[roomIndex].get();
            if (boxRoomHandle != null) {
                this.allRoomHandle.remove(boxRoomHandle);
            }
        }
    }

    /**
     * 创建自定义包厢
     * 
     * @param player
     * @param gameType
     * @param gameSubType
     * @param rule
     * @param remarks
     * @return
     */
    public ErrorCode createCustomRoom(Player player, int gameType, int gameSubType,
            HashMap<String, Integer> rule, String remarks) {
        if (EBoxState.INIT != this.state.get()) {
            Logs.GROUP.warn("%s 正处于关闭/关闭中, 无法创建自定义包厢", this);
            return ErrorCode.GROUP_BOX_CLOSE;
        }
        if (!EBoxType.CUSTOM.match(this.boxType)) {
            return ErrorCode.REQUEST_INVALID;
        }
        IRoom room = RoomManager.I.createBoxRoom(player, this, gameType, gameSubType, rule, remarks);
        if (null == room) {
            Logs.GROUP.warn("创建房间失败%d", gameType);
            return ErrorCode.GM_INVALID_OPERATE;
        }
        room.setRoomType(ERoomType.BOX);
        // 创建房间业务逻辑处理器
        room.setRoomHandle(RoomHandleFactory.createRoomHandle(room, this));
        IBoxRoomHandle boxRoomHandle = (IBoxRoomHandle) room.getRoomHandle();
        allRoomHandle.register(boxRoomHandle);
        if (boxRoomHandle.getIndex() == -1) {
            return ErrorCode.GROUP_BOX_MAX;
        }
        this.onCreateRoom(room);

        PCLIBoxNtfChangeStateInfo changeStateInfo = new PCLIBoxNtfChangeStateInfo();
        changeStateInfo.stateInfo.roomIndex = boxRoomHandle.getIndex();
        changeStateInfo.stateInfo.roomInfo = ((Room) room).getBoxRoomInfo(this.getExtra());
        changeStateInfo.boxUid = this.uid;
        changeStateInfo.groupUid = this.ownerUid;
        player.send(CommandId.CLI_NTF_BOX_ROOM_CHANGE_STATE, changeStateInfo);
    
        return this.join(player, boxRoomHandle.getIndex());
    }

    /**
     * 关闭
     * 
     * @return
     */
    public boolean close() {
        if (this.state.compareAndSet(EBoxState.INIT, EBoxState.WAIT_CLOSE)) {
            this.allBoxHandles.clear();
            this.waitClose = true;
            this.killAllIdleRoom();
            this.checkClose();
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private void checkClose() {
        if (0 == this.allPlayer.size()) {
            if (this.state.compareAndSet(EBoxState.WAIT_CLOSE, EBoxState.CLOSE)) {
                try {
                    List<IBoxRoomHandle> roomHandleList = null;
                    AsynchronousQueueLock<IBoxRoomHandle>[] fixedAsynchronousSource = this.allRoomHandle.getSource();
                    for (int i = 0, len = fixedAsynchronousSource.length; i < len; ++i) {
                        AsynchronousQueueLock<IBoxRoomHandle> asynchronousQueueLock = fixedAsynchronousSource[i];
                        ReentrantLock spaceLock = asynchronousQueueLock.getSpaceLock();
                        try {
                            // 得到锁
                            spaceLock.lock();
                            if (asynchronousQueueLock.get() != null) {
                                if (null == roomHandleList) {
                                    roomHandleList = new ArrayList<IBoxRoomHandle>();
                                }
                                roomHandleList.add(asynchronousQueueLock.get());
                                asynchronousQueueLock.remove();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (spaceLock.isHeldByCurrentThread()) {
                                spaceLock.unlock();
                            }
                        }
                    }
                    if (roomHandleList != null) {
                        for (IBoxRoomHandle boxRoomHandle : roomHandleList) {
                            IRoom room = boxRoomHandle.getRoom();
                            ((Room)room).setDestroyType(ERoomDestroyType.MANAGER_CLOSE_BOX_DESTROY);
                            room.destroy();
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
                this.currThread.deAttackTick(this);
                this.allBoxHandles.clear();
                BoxManager.I.destroyBox(this.ownerUid, this.uid);
            }
        }
    }

    public void killAllIdleRoom() {
        try {
            List<IBoxRoomHandle> roomHandleList = null;
            AsynchronousQueueLock<IBoxRoomHandle>[] fixedAsynchronousSource = this.allRoomHandle.getSource();
            for (int i = 0, len = fixedAsynchronousSource.length; i < len; ++i) {
                AsynchronousQueueLock<IBoxRoomHandle> asynchronousQueueLock = fixedAsynchronousSource[i];
                ReentrantLock spaceLock = asynchronousQueueLock.getSpaceLock();
                try {
                    // 得到锁
                    spaceLock.lock();
                    if (asynchronousQueueLock.get() != null && asynchronousQueueLock.get().getRoom().isNew()) {
                        if (null == roomHandleList) {
                            roomHandleList = new ArrayList<IBoxRoomHandle>();
                        }
                        roomHandleList.add(asynchronousQueueLock.get());
                        asynchronousQueueLock.remove();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (spaceLock.isHeldByCurrentThread()) {
                        spaceLock.unlock();
                    }
                }
            }
            if (roomHandleList != null && roomHandleList.size() > 0) {
                for (IBoxRoomHandle roomHandle : roomHandleList) {
                    roomHandle.getRoom().destroy();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addWatchPlayer(Player player) {
        this.allWatchPlayer.putIfAbsent(player.getUid(), player);
    }

    public void delWatchPlayer(Player player) {
        this.allWatchPlayer.remove(player.getUid());
    }

    @JSONField(serialize = false)
    public PCLIBoxInfo getBoxInfo() {
        PCLIBoxInfo boxInfo = new PCLIBoxInfo();
        boxInfo.boxUid = this.uid;
        boxInfo.ownerUid = this.ownerUid;
        boxInfo.boxType = this.boxType;
        boxInfo.boxName = this.boxName;
        boxInfo.gameType = this.gameType;
        boxInfo.gameSubType = this.gameSubType;
        boxInfo.allPlayerCnt = this.allPlayer.size();
        boxInfo.bWaitClose = this.waitClose;
        boxInfo.rule.putAll(this.rule);
        boxInfo.extra.putAll(this.extra);
        return boxInfo;
    }

    /**
     * 获取玩法桌信息列表
     * 
     * @param tempList
     */
    @JSONField(serialize = false)
    public void getPlayRoomStateInfo(List<PCLIBoxRoomStateInfo> tempList, int type) {
        try {
            PCLIBoxRoomStateInfo stateInfo = new PCLIBoxRoomStateInfo();
            stateInfo.boxUid = this.uid;
            stateInfo.type = type;
            stateInfo.roomIndex = 0;
            stateInfo.boxType = this.boxType;
            stateInfo.gameType = this.gameType;
            stateInfo.gameSubType = this.getGameSubType();
            stateInfo.endPoint = getRoomEndPoint(stateInfo.gameType,stateInfo.gameSubType,this.getRule());
            stateInfo.endPointMul =  this.rule.getOrDefault(RoomRule.RR_END_POINT,0);//注释
            stateInfo.playType = getRoomPlayTypeForClient(stateInfo.gameType,stateInfo.gameSubType,this.getRule());
            tempList.add(stateInfo);
        } finally {
        }
    }

    @JSONField(serialize = false)
    public void getRoomState(List<PCLIBoxRoomStateInfo> tempList, int type) {
        try {
            HashSet<Integer> ids = new HashSet<Integer>();
            // 固定模式玩法桌：第一个未开始的游戏桌与它对应的玩法桌合并显示，其他未开始的游戏桌独立显示
            AsynchronousQueueLock<IBoxRoomHandle>[] fixedAsynchronousSource = this.allRoomHandle.getSource();
            for (int i = 0, len = fixedAsynchronousSource.length; i < len; ++i) {
                AsynchronousQueueLock<IBoxRoomHandle> asynchronousQueueLock = fixedAsynchronousSource[i];
                IBoxRoomHandle roomHandle = asynchronousQueueLock.get();
                if (null == roomHandle) {
                    continue;
                }
                IRoom room = roomHandle.getRoom();
                if (room.isStart() || room.isAgain()) {
                    continue;
                }

                for (PCLIBoxRoomStateInfo playInfo : tempList) {
                    // 是否是固定模式玩法桌
                    if (playInfo.type == 2 && playInfo.boxUid == this.uid) {
                        playInfo.roomInfo = ((Room) room).getBoxRoomInfo(this.getExtra());
                        playInfo.roomIndex = i;
                        playInfo.gameType = this.gameType;
                        playInfo.gameSubType = this.getGameSubType();
                        playInfo.endPoint = getRoomEndPoint(playInfo.gameType,playInfo.gameSubType,this.getRule());
                        playInfo.endPointMul =  this.rule.getOrDefault(RoomRule.RR_END_POINT,0);//注释
                        playInfo.playType = getRoomPlayTypeForClient(playInfo.gameType,playInfo.gameSubType,this.getRule());
                        ids.add(i);
                        break;
                    }
                }
                if (ids.size() > 0) {
                    break;
                }
            }

            // 优先显示未开始的游戏桌
            for (int i = 0, len = fixedAsynchronousSource.length; i < len; ++i) {
                AsynchronousQueueLock<IBoxRoomHandle> asynchronousQueueLock = fixedAsynchronousSource[i];
                IBoxRoomHandle roomHandle = asynchronousQueueLock.get();
                if (null == roomHandle) {
                    continue;
                }
                if (ids.contains(i)) {
                    continue;
                }
                IRoom room = roomHandle.getRoom();
                boolean isStart = room.isStart() || room.isAgain();
                if (isStart) {
                    continue;
                }
                PCLIBoxRoomStateInfo stateInfo = new PCLIBoxRoomStateInfo();
                stateInfo.boxUid = this.uid;
                stateInfo.isStart = isStart;
                stateInfo.type = type;
                stateInfo.roomIndex = i;
                stateInfo.roomInfo = ((Room) room).getBoxRoomInfo(this.getExtra());
                stateInfo.boxType = this.boxType;
                stateInfo.gameType = this.gameType;
                stateInfo.gameSubType = this.getGameSubType();
                stateInfo.endPoint = getRoomEndPoint(stateInfo.gameType,stateInfo.gameSubType,this.getRule());
                stateInfo.endPointMul =  this.rule.getOrDefault(RoomRule.RR_END_POINT,0);//注释
                stateInfo.playType = getRoomPlayTypeForClient(stateInfo.gameType,stateInfo.gameSubType,this.getRule());
                tempList.add(stateInfo);
            }
            // 最后显示已开始的游戏桌
            for (int i = 0, len = fixedAsynchronousSource.length; i < len; ++i) {
                AsynchronousQueueLock<IBoxRoomHandle> asynchronousQueueLock = fixedAsynchronousSource[i];
                IBoxRoomHandle roomHandle = asynchronousQueueLock.get();
                if (null == roomHandle) {
                    continue;
                }
                IRoom room = roomHandle.getRoom();
                if (ids.contains(i)) {
                    continue;
                }
                boolean isStart = room.isStart() || room.isAgain();
                if (isStart) {
                    PCLIBoxRoomStateInfo stateInfo = new PCLIBoxRoomStateInfo();
                    stateInfo.boxUid = this.uid;
                    stateInfo.isStart = isStart;
                    stateInfo.type = type;
                    stateInfo.roomIndex = i;
                    stateInfo.roomInfo = ((Room) room).getBoxRoomInfo(this.getExtra());
                    stateInfo.boxType = this.boxType;
                    stateInfo.gameType = this.gameType;
                    stateInfo.gameSubType = this.getGameSubType();
                    stateInfo.endPoint = getRoomEndPoint(stateInfo.gameType,stateInfo.gameSubType,this.getRule());
                    stateInfo.endPointMul =  this.rule.getOrDefault(RoomRule.RR_END_POINT,0);//注释
                    stateInfo.playType = getRoomPlayTypeForClient(stateInfo.gameType,stateInfo.gameSubType,this.getRule());
                    tempList.add(stateInfo);
                }
            }
        } finally {
        }
    }

    public void broadcast(int commandId, Object message) {
        Iterator<Map.Entry<Long, Player>> it = this.allWatchPlayer.entrySet().iterator();
        while (it.hasNext()) {
            Player boxPlayer = it.next().getValue();
            if (!boxPlayer.isOnline()) {
                it.remove();
                continue;
            }
            boxPlayer.send(commandId, message);
        }
    }

    public void broadcast(int commandId, Object message, long playerUid) {
        Iterator<Map.Entry<Long, Player>> it = this.allWatchPlayer.entrySet().iterator();
        while (it.hasNext()) {
            Player boxPlayer = it.next().getValue();
            if (boxPlayer.getUid() == playerUid) {
                continue;
            }
            if (!boxPlayer.isOnline()) {
                it.remove();
                continue;
            }
            boxPlayer.send(commandId, message);
        }
    }

    /**
     * 游戏底分
     * @param gameType
     * @param gameSubType
     * @param rule
     * @return
     */
    public static int getRoomEndPoint(int gameType,int gameSubType,Map<String,Integer> rule){
        if (gameType == GameType.GAME_TYPE_HUNDRED_LHD
                || gameType == GameType.GAME_TYPE_HUNDRED_BACCARAT){
            return rule.getOrDefault(RoomRule.RR_HUNDRED_UP_BANKER_VALUE, 0);
        }
        if (gameType == GameType.GAME_TYPE_SG){
            return rule.getOrDefault(RoomRule.RR_SG_END_POINT,1);
        }
        if (gameType == GameType.GAME_TYPE_PAIGOW){
            if (gameSubType == 0){
                return rule.getOrDefault(RoomRule.RR_COW_END_POINT, 0);
            }else if (gameSubType == 1){
                return rule.getOrDefault(RoomRule.RR_PAIGOW_MINBETS_RULE,5);
            }
        }
        if (gameType == GameType.GAME_TYPE_COW){
            if (gameSubType == 1){
                return rule.getOrDefault(RoomRule.RR_COW_PORKER_CARD_HOT_NOTE, 0);
            }else if (gameSubType == 2){
                return rule.getOrDefault(RoomRule.RR_COW_END_POINT,0);
            }
        }
        if (gameType == GameType.GAME_TYPE_FRIED_GOLDEN_FLOWER){
            return rule.getOrDefault("displayBottomScore",0);
        }
        return rule.getOrDefault(RoomRule.RR_END_POINT,0);
    }

    /**
     * 客户端显示桌面需要区分的游戏玩法
     * @param gameType
     * @param gameSubType
     * @param rule
     * @return
     */
    public static int getRoomPlayTypeForClient(int gameType,int gameSubType,Map<String,Integer> rule){
        if (gameType == GameType.GAME_TYPE_LANDLORD){
            return rule.getOrDefault(RoomRule.RR_LANDLORD_PLAY, 0);
        }
        if (gameType == GameType.GAME_TYPE_PAIGOW && gameSubType == 0){
            return rule.getOrDefault(RoomRule.RR_PAIGOW_GAMEPLAY_PAGE, 2);
        }
        return -1;
    }

    public int getBoxType() {
        return boxType;
    }

    public void setBoxType(int boxType) {
        this.boxType = boxType;
    }

    public String getBoxName() {
        return boxName;
    }

    public void setBoxName(String boxName) {
        this.boxName = boxName;
    }

    public int getGameSubType() {
        return gameSubType;
    }

    public void setGameSubType(int gameSubType) {
        this.gameSubType = gameSubType;
    }

    public int getGameType() {
        return gameType;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public HashMap<String, Integer> getRule() {
        return rule;
    }

    public void setRule(HashMap<String, Integer> rule) {
        this.rule = rule;
    }

    public HashMap<String, String> getExtra() {
        return extra;
    }

    public String getRuleDb() {
        return JsonUtil.toJson(this.rule);
    }

    public void setRuleDb(String rule) {
        this.rule = JsonUtil.fromJson(rule, new TypeReference<HashMap<String, Integer>>() {
        });
    }

    public String getExtraDb() {
        return JsonUtil.toJson(this.extra);
    }

    public void setExtraDb(String extra) {
        this.extra = JsonUtil.fromJson(extra, new TypeReference<HashMap<String, String>>() {
        });
    }

    /**
     * 修改包厢规则
     * 
     * @param rule
     * @param extra
     */
    public void modifyRule(HashMap<String, Integer> rule, HashMap<String, String> extra) {
        if (null != rule) {
            this.rule.putAll(rule);
        }
        if (null != extra) {
            this.extra.putAll(extra);
        }
    }

    @JSONField(serialize = false)
    public EBoxState getState() {
        return this.state.get();
    }

    @JSONField(serialize = false)
    public int getAllPlayerCnt() {
        return this.allPlayer.size();
    }
    
    @JSONField(serialize = false)
    public void clearAllPlayer() {
        this.allPlayer.clear();
    }

    public long getFloorUid() {
        return floorUid;
    }

    public void setFloorUid(long floorUid) {
        this.floorUid = floorUid;
    }

    public int getGainValue() {
        return this.rule.getOrDefault(RoomRule.RR_GAIN_VALUE, 0);
    }

    public int getWinGainValue() {
        return this.rule.getOrDefault(RoomRule.RR_WIN_GAIN_VALUE, 0);
    }

    public IRoomPlayer getRoomPlayer(long playerUid) {
        return this.allPlayer.get(playerUid);
    }

    public long getOwnerUid() {
        return ownerUid;
    }

    public void setOwnerUid(long ownerUid) {
        this.ownerUid = ownerUid;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(int ownerType) {
        this.ownerType = ownerType;
    }

    @Override
    public String toString() {
        return String.format("Box[BoxUid:%d]", this.getUid());
    }

    /**
     * 坐下
     *
     * @param player
     * @param sitIndex
     * @return
     */
    public ErrorCode sitDown(Player player, int sitIndex) {
        if (this.state.get() == EBoxState.WAIT_CLOSE || this.state.get() == EBoxState.CLOSE) {
            Logs.GROUP.info("%s 已经关闭, 无法坐下", this);
            return ErrorCode.ARENA_CLOSE;
        }
        IRoomPlayer boxPlayer = this.allPlayer.get(player.getUid());
        if (null == boxPlayer) {
            Logs.GROUP.warn("%s %s 不在包厢中, 无法坐下", this, player);
            return ErrorCode.ROOM_NOT_IN;
        }
        long roomUid = boxPlayer.getRoomUid();
        IBoxHandle handle = allBoxHandles.get(roomUid);
        if (null == handle) {
            return ErrorCode.ROOM_NOT_IN;
        }
        return handle.sitDown(boxPlayer, sitIndex);
    }

    /**
     * 站起
     *
     * @param player
     * @return
     */
    public ErrorCode sitUp(Player player) {
        if (this.state.get() == EBoxState.WAIT_CLOSE || this.state.get() == EBoxState.CLOSE) {
            Logs.GROUP.info("%s 已经关闭, 无法坐下", this);
            return ErrorCode.ARENA_CLOSE;
        }
        IRoomPlayer boxPlayer = this.allPlayer.get(player.getUid());
        if (null == boxPlayer) {
            // 提示：没有房间中
            return ErrorCode.ROOM_NOT_IN;
        }
        long roomUid = boxPlayer.getRoomUid();
        IBoxHandle handle = allBoxHandles.get(roomUid);
        if (null == handle) {
            return ErrorCode.ROOM_NOT_IN;
        }
        return handle.sitUp(boxPlayer);
    }

    /**
     * 准备
     *
     * @param player
     * @return
     */
    public ErrorCode ready(Player player) {
        if (this.state.get() == EBoxState.WAIT_CLOSE || this.state.get() == EBoxState.CLOSE) {
            Logs.GROUP.info("%s 已经关闭, 无法准备", this);
            return ErrorCode.ARENA_CLOSE;
        }
        IRoomPlayer boxPlayer = this.allPlayer.get(player.getUid());
        if (null == boxPlayer) {
            // 提示：没有房间中
            return ErrorCode.ROOM_NOT_IN;
        }
        long roomUid = boxPlayer.getRoomUid();
        IBoxHandle handle = allBoxHandles.get(roomUid);
        if (null == handle) {
            return ErrorCode.ROOM_NOT_IN;
        }
        return handle.ready(player, boxPlayer);
    }

    public void resetSitUpTime(IRoom room) {
        IBoxHandle handle = allBoxHandles.get(room.getRoomUid());
        if (null == handle) {
            return;
        }
        handle.resetSitUpTime();
    }

    public void tick(long curTime, long delay, IRoom room) {
        if (allBoxHandles.isEmpty()) {
            return;
        }
        IBoxHandle handle = allBoxHandles.get(room.getRoomUid());
        if (null == handle) {
            return;
        }
        handle.tick();
    }
    
    
    /**
     * 是否存在已开始的游戏桌
     * @return
     */
    public boolean existStartedGameDesk() {
        try {
            AsynchronousQueueLock<IBoxRoomHandle>[] fixedAsynchronousSource = this.allRoomHandle.getSource();
            for (int i = 0, len = fixedAsynchronousSource.length; i < len; ++i) {
                AsynchronousQueueLock<IBoxRoomHandle> asynchronousQueueLock = fixedAsynchronousSource[i];
                IRoomHandle boxRoomHandle = asynchronousQueueLock.get();
                if (null == boxRoomHandle) {
                    continue;
                }
                IRoom room = boxRoomHandle.getRoom();
                if (room.getRoomState() == ERoomState.START) {
                    return Boolean.TRUE;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }
    
    public IBoxHandle getBoxHandle(long roomUid) {
        return allBoxHandles.get(roomUid);
    }

    public List<Long> getRoomIds() {
    	List<Long> list = new ArrayList<Long>();
    	for(ConcurrentHashMap.Entry<Long, IBoxHandle> entry : this.allBoxHandles.entrySet()) {
    		Long roomUid = entry.getKey();
    		list.add(roomUid);
    	}
        return list;
    }

	public FixedAsynchronousQueue<IBoxRoomHandle> getAllRoomHandle() {
		return allRoomHandle;
	}

	/**
     * 获取观战名单
     *
     * @param player
     * @return
     */
    public ErrorCode getAllWatchPlayer(Player player) {
    	if (this.state.get() == EBoxState.WAIT_CLOSE || this.state.get() == EBoxState.CLOSE) {
            Logs.GROUP.info("%s 已经关闭, 无法获取观战名单", this);
            return ErrorCode.ARENA_CLOSE;
        }
        IRoomPlayer boxPlayer = this.allPlayer.get(player.getUid());
        if (null == boxPlayer) {
            // 提示：没有房间中
            return ErrorCode.ROOM_NOT_IN;
        }
        long roomUid = boxPlayer.getRoomUid();
        IBoxHandle handle = allBoxHandles.get(roomUid);
        if (null == handle) {
            return ErrorCode.ROOM_NOT_IN;
        }
        return handle.getAllWatchPlayer(boxPlayer);
    }
	
}
