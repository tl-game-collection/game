package com.xiuxiu.app.server.box.handle;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.box.PCLIArenaNtfChangeMatchState;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.constant.EBoxType;
import com.xiuxiu.app.server.forbid.ForbidManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.ERoomState;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.handle.IBoxRoomHandle;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.app.server.room.normal.poker.cow.CowHotRoom;
import com.xiuxiu.core.ds.ConcurrentHashSet;

/**
 * 亲友圈可少人模式处理实现
 * 
 * @author Administrator
 *
 */
public class WatchBoxHandle extends AbstractBoxHandle {

    /**
     * 观战的玩家id列表
     */
    protected ConcurrentHashSet<Long> allWatchPlayer = new ConcurrentHashSet<>();
    /**
     * 坐下观战的玩家id列表
     */
    private ConcurrentHashMap<Long, Long> allSitDownPlayer = new ConcurrentHashMap<>();
    /**
     * 当前房间参与玩过的玩家id列表
     */
    private ConcurrentHashSet<Long> allPlayedPlayer = new ConcurrentHashSet<>();

    public WatchBoxHandle(Box box, IRoom room) {
        super(box, room);
    }

    @Override
    public ErrorCode sitDown(IRoomPlayer roomPlayer, int index) {
        if (allSitDownPlayer.contains(roomPlayer.getUid())) {
            Logs.GROUP.warn("%s %s 已经坐下", this, roomPlayer);
            return ErrorCode.ARENA_ALREADY_SIT_DOWN;
        }
//        // 是否玩过
//        if (((Room)room).hasPlayed(boxPlayer.getUid())) {
//            Logs.GROUP.warn("%s %s 已经坐下", this, boxPlayer);
//            return ErrorCode.ARENA_ALREADY_SIT_DOWN;
//        }
        if (this.allPlayedPlayer.contains(roomPlayer.getUid())) {
            Logs.ARENA.warn("%s %s 已经坐下", this, roomPlayer);
            return ErrorCode.ARENA_ALREADY_SIT_DOWN;
        }
        boolean isCheckIp = this.isCheckIpSame(roomPlayer);
        if (isCheckIp) {
            Logs.ARENA.warn("%s %s IP相同无法坐下", this, roomPlayer);
            return ErrorCode.CHECK_IP_SAME_STDOWN;
        }
        ErrorCode err = isGroupForbid(roomPlayer);
        if (ErrorCode.OK != err) {
            return err;
        }
        err = this.room.sitDown(roomPlayer, index);
        if (ErrorCode.OK == err) {
            this.allSitDownPlayer.put(roomPlayer.getUid(), System.currentTimeMillis());
            this.allWatchPlayer.remove(roomPlayer.getUid());
            Logs.ARENA.info("%s %s 坐下成功", this, roomPlayer);
        }
        return err;
    }

    @Override
    public ErrorCode sitUp(IRoomPlayer roomPlayer) {
//        if (!allSitDownPlayer.contains(roomPlayer.getUid())) {
//            Logs.GROUP.warn("%s %s 已经坐下", this, roomPlayer);
//            return ErrorCode.ARENA_ALREADY_SIT_DOWN;
//        }
        if (room.getCurBureau() > 0) {
//            // 是否玩过
//            if (!((Room)room).hasPlayed(roomPlayer.getUid())) {
                roomPlayer.setGuest(true);
                ErrorCode err = this.room.sitUp(roomPlayer, false);
                if (ErrorCode.OK == err) {
                    this.notifyChangeState(roomPlayer.getUid(), false);
                    this.allSitDownPlayer.remove(roomPlayer.getUid());
                    this.allPlayedPlayer.remove(roomPlayer.getUid());
                    this.allWatchPlayer.add(roomPlayer.getUid());
                    Logs.ARENA.info("%s %s 站起成功", this, roomPlayer);
                    this.room.checkStart();
                }
                return err;
//            }
        } else {
            if (room.getRoomState() != ERoomState.START) {
                roomPlayer.setGuest(true);
                ErrorCode err = this.room.sitUp(roomPlayer, false);
                if (ErrorCode.OK == err) {
                    this.notifyChangeState(roomPlayer.getUid(), false);
                    this.allSitDownPlayer.remove(roomPlayer.getUid());
                    this.allPlayedPlayer.remove(roomPlayer.getUid());
                    this.allWatchPlayer.add(roomPlayer.getUid());
                    Logs.ARENA.info("%s %s 站起成功", this, roomPlayer);
                    this.room.checkStart();
                }
                return err;
            }
        }
        return ErrorCode.ROOM_SIT_UP_LIMIT;
    }

    private void notifyChangeState(long playerUid, boolean ready) {
        PCLIArenaNtfChangeMatchState state = new PCLIArenaNtfChangeMatchState();
        state.arenaUid = this.box.getUid();
        state.groupUid = this.box.getOwnerUid();
        state.matchUid = this.room.getRoomUid();
        state.ready = ready;
        state.playerUid = playerUid;
        this.broadcast(CommandId.CLI_NTF_ARENA_MATCH_CHANGE_STATE, state);
    }

    private void broadcast(int cmd, Object message) {
        for (Long uid : this.allPlayedPlayer) {
            IRoomPlayer player = this.box.getRoomPlayer(uid);
            if (null == player || player.isOffline()) {
                continue;
            }
            player.send(cmd, message);
        }
    }
    
    private ErrorCode isGroupForbid(IRoomPlayer player) {
        ErrorCode err = ForbidManager.I.isForbid(this.box.getOwnerType(), box.getOwnerUid(), player.getUid(), this.allSitDownPlayer.keySet());
        if(err != ErrorCode.OK) {
            return err;
        }
        
        err = ForbidManager.I.isForbid(this.box.getOwnerType(), box.getOwnerUid(), player.getUid(), this.allPlayedPlayer);
        return err;
    }

    private boolean isCheckIpSame(IRoomPlayer player) {
        //百人场和竞技场不检测
        if (EBoxType.ARENA.match(this.box.getBoxType()) || EBoxType.HUNDRED.match(this.box.getBoxType())) {
            return false;
        }
        Player player1 = PlayerManager.I.getPlayer(player.getUid());
        if (null == player1) {
            return false;
        }
        boolean isCheckIp = this.room.getDetectionIP();
//                0 != (this.room.getRule().getOrDefault(RoomRule.RR_PLAY, 0)
//                & (this.room.getGameType() == GameType.GAME_TYPE_RUN_FAST ? 0x00080 : 0x00400));
        for (Long temp : this.allSitDownPlayer.keySet()) {
            if (null != temp) {
                Player player2 = PlayerManager.I.getPlayer(temp);
                if (null != player2 && isCheckIp && player2.getLogngIp().equals(player1.getLogngIp())) {
                    return true;
                }
            }
        }
        for (Long temp : this.allPlayedPlayer) {
            if (null != temp) {
                Player player2 = PlayerManager.I.getPlayer(temp);
                if (null != player2 && isCheckIp && player2.getLogngIp().equals(player1.getLogngIp())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ErrorCode ready(Player player, IRoomPlayer roomPlayer) {
        IRoomPlayer tempRoomPlayer = this.room.getRoomPlayer(player.getUid());
        if (null == tempRoomPlayer) {
            return null;
        }
        if (room.getGameType() == GameType.GAME_TYPE_COW && room.getGameSubType() == 1){
            if(((CowHotRoom)room).getCurPhase() != 0){
                if (tempRoomPlayer.isGuest()){
                    return ErrorCode.ARENA_LESS_THAN_MIN_VALUE;
                }else{
                    return ErrorCode.REQUEST_INVALID_DATA;
                }
            }
        }
        if (allSitDownPlayer.containsKey(player.getUid())) {
            ((Room)room).clearGuest(roomPlayer);
        }
        // 是否是观战玩家
        if (this.room.isWatchPlayer(roomPlayer)) {
            return ErrorCode.PLAYER_NOT_EXISTS;
        }
        if (!allSitDownPlayer.isEmpty() && !allSitDownPlayer.containsKey(roomPlayer.getUid())
                && (!this.allPlayedPlayer.isEmpty() && !this.allPlayedPlayer.contains(roomPlayer.getUid()))) {
            Logs.ARENA.warn("%s %s 无法准备", room, roomPlayer);
            return ErrorCode.PLAYER_NOT_EXISTS;
        }
        ErrorCode err = this.room.ready(player);
        if (ErrorCode.OK != err) {
            return err;
        }
        this.allPlayedPlayer.add(player.getUid());
        this.allSitDownPlayer.remove(player.getUid());
        this.allWatchPlayer.remove(player.getUid());
        return ErrorCode.OK;
    }

    @Override
    public ErrorCode onJoin(IRoomPlayer roomPlayer) {
        if (room.getRoomState() == ERoomState.START) {
            room.join(PlayerManager.I.getOnlinePlayer(roomPlayer.getUid()));
            this.allWatchPlayer.add(roomPlayer.getUid());
            return ErrorCode.OK;
        } else {
            synchronized (this) {
                if (this.room.isFull()) {
                    if (room.getRoomState() != ERoomState.FINISH && room.getRoomState() != ERoomState.DESTROY) {
                        room.join(PlayerManager.I.getOnlinePlayer(roomPlayer.getUid()));
                        this.allWatchPlayer.add(roomPlayer.getUid());
                        return ErrorCode.OK;
                    } else {
                        return ErrorCode.GROUP_BOX_NOT_EXISTS;
                    }
                } else {
                    // 加入房间
                    IBoxRoomHandle boxRoomHandle = (IBoxRoomHandle) room.getRoomHandle();
                    return boxRoomHandle.join(roomPlayer);
                }
            }
        }
    }

    @Override
    public void killAll(Box box) {
        if (!allPlayedPlayer.isEmpty()) {
            for (Long tempId : allPlayedPlayer) {
                IRoomPlayer roomPlayer = box.getRoomPlayer(tempId);
                if (roomPlayer != null) {
                    roomPlayer.setRoom(null);
                }
            }
        }
        if (!allSitDownPlayer.isEmpty()) {
            for (Long tempId : allSitDownPlayer.keySet()) {
                IRoomPlayer roomPlayer = box.getRoomPlayer(tempId);
                if (roomPlayer != null) {
                    roomPlayer.setRoom(null);
                }
            }
        }
        if (!allWatchPlayer.isEmpty()) {
            for (Long tempId : allWatchPlayer) {
                IRoomPlayer roomPlayer = box.getRoomPlayer(tempId);
                if (roomPlayer != null) {
                    roomPlayer.setRoom(null);
                }
            }
        }
        allPlayedPlayer.clear();
        allSitDownPlayer.clear();
        allWatchPlayer.clear();
    }

    @Override
    public void level(long playerUid) {
        allPlayedPlayer.remove(playerUid);
        allSitDownPlayer.remove(playerUid);
        allWatchPlayer.remove(playerUid);

    }

    @Override
    public void resetSitUpTime() {
        long now = System.currentTimeMillis();
        if (!allSitDownPlayer.isEmpty()) {
            Iterator<Map.Entry<Long, Long>> sitdownIt = this.allSitDownPlayer.entrySet().iterator();
            while (sitdownIt.hasNext()) {
                Map.Entry<Long, Long> entry = sitdownIt.next();
                entry.setValue(now);
            }
        }
        Set<Long> tempUids = this.room.getGuestPlayerUids();
        if (tempUids != null) {
            for (long tempUid :tempUids) {
                allSitDownPlayer.put(tempUid, now);
            }
        }
    }

    public void tick() {
        if (this.room != null && this.room.getRoomState() != ERoomState.START && this.room.getRoomState() != ERoomState.DESTROY) {
            if (!GameType.isArenaGame(this.room.getGameType())) {
                autoSitUp();
            }
        }
    }

    /**
     * 自动站起
     */
    private void autoSitUp() {
        try {
            if (!allSitDownPlayer.isEmpty()) {
                Iterator<Map.Entry<Long, Long>> sitdownIt = this.allSitDownPlayer.entrySet().iterator();
                long now = System.currentTimeMillis();
                while (sitdownIt.hasNext()) {
                    Map.Entry<Long, Long> entry = sitdownIt.next();
                    if (this.allPlayedPlayer.contains(entry.getKey())) {
                        continue;
                    }
                    if (((Room) this.room).isReady(entry.getKey())) {
                        continue;
                    }
                    if (now - entry.getValue() >= 15000) {
                        sitdownIt.remove();
                        IRoomPlayer player = this.box.getRoomPlayer(entry.getKey());
                        if (null != player) {
                            sitUp(player);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logs.ROOM.warn("包厢坐下的玩家自动站起异常", e);
        }
    }
    
    @Override
    public Map<Long, Long> allSitDownPlayer() {
        return this.allSitDownPlayer;
    }

	@Override
	public IRoom getRoom() {
		return this.room;
	}
	
	@Override
    public ErrorCode getAllWatchPlayer(IRoomPlayer roomPlayer) {
		roomPlayer.send(CommandId.CLI_REQ_BOX_ALL_WATCH_PLAYER_OK, this.allWatchPlayer);
		return ErrorCode.OK;
	}
	
}
