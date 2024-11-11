package com.xiuxiu.app.server.room.normal;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.player.helper.IRoomPlayerHelper;
import com.xiuxiu.app.server.room.player.helper.RoomPlayerHelperFactory;
import com.xiuxiu.core.utils.Location;

public class RoomPlayer implements IRoomPlayer {
    protected long playerUid = -1;
    protected long roomUid = -1;
    protected int roomId = -1;
    protected int index;
    protected boolean hosting;
    protected boolean guest = false;
    protected int operationTimeoutCnt;
    protected HashMap<String, Integer> accScore = new HashMap<>();
    protected HashMap<String, Integer> score = new HashMap<>();

    protected AtomicReference<EState> state = new AtomicReference<>(EState.ONLINE); // 在线

    protected boolean over = false;
    protected boolean isAutoMode = false; // true:托管，false:解除托管
    
    /**
     * 游戏类型
     */
    private int gameType;

    private IRoomPlayerHelper roomPlayerHelper;

    public RoomPlayer(int gameType, long roomUid, int roomId) {
        this.gameType = gameType;
        this.roomUid = roomUid;
        this.roomId = roomId;
    }

    @Override
    public void doStart() {
    }

    @Override
    public boolean changeState(EState state) {
        Logs.ROOM.debug("%s oldState:%s newState:%s", this, this.state.get(), state);
        if (EState.ONLINE == state) {
            if (this.state.compareAndSet(EState.LEAVE, state) || this.state.compareAndSet(EState.OFFLINE, state)) {
                return true;
            }
        } else if (EState.OFFLINE == state) {
            if (this.state.compareAndSet(EState.ONLINE, state)) {
                return true;
            }
        } else if (EState.LEAVE == state) {
            if (this.state.compareAndSet(EState.ONLINE, state)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setHosting(boolean hosting) {
        this.hosting = hosting;
    }

    @Override
    public boolean isHosting(int timeout) {
        if (this.hosting) {
            return true;
        }
        return -1 != timeout && EState.ONLINE != this.state.get();
    }

    @Override
    public long getTimeout(long timeout) {
        if (this.operationTimeoutCnt >= 2) {
            this.setAutoMode(true);
            return 3 * 1000;
        }
        return timeout;
    }

    @Override
    public void setScore(String score, int value, boolean isAcc) {
        if (isAcc) {
            this.accScore.put(score, value);
        } else {
            this.score.put(score, value);
        }
    }

    @Override
    public void addScore(String score, int value, boolean isAcc) {
        if (isAcc) {
            int oldValue = this.accScore.getOrDefault(score, 0);
            this.accScore.put(score, value + oldValue);
        } else {
            int oldValue = this.score.getOrDefault(score, 0);
            this.score.put(score, value + oldValue);
        }
    }

    @Override
    public void maxScore(String score, int value, boolean isAcc) {
        if (isAcc) {
            int oldValue = this.accScore.getOrDefault(score, Integer.MIN_VALUE);
            if (value > oldValue) {
                this.accScore.put(score, value);
            }
        } else {
            int oldValue = this.score.getOrDefault(score, Integer.MIN_VALUE);
            if (value > oldValue) {
                this.score.put(score, value);
            }
        }
    }

    @Override
    public void minScore(String score, int value, boolean isAcc) {
        if (isAcc) {
            int oldValue = this.accScore.getOrDefault(score, Integer.MAX_VALUE);
            if (value < oldValue) {
                this.accScore.put(score, value);
            }
        } else {
            int oldValue = this.score.getOrDefault(score, Integer.MAX_VALUE);
            if (value < oldValue) {
                this.score.put(score, value);
            }
        }
    }

    @Override
    public int getScore(String score, boolean isAcc) {
        int value = isAcc ? this.accScore.getOrDefault(score, 0) : this.score.getOrDefault(score, 0);
        if (Integer.MAX_VALUE == value || Integer.MIN_VALUE == value) {
            value = 0;
        }
        return value;
    }

    @Override
    public int getScore() {
        return getRoomPlayerHelper().getScore();
    }

    @Override
    public int getBureau() {
        Room room = RoomManager.I.getRoom(this.roomId);
        return null == room ? -1 : room.getCurBureau();
    }

    @Override
    public void operationTimeout() {
        ++this.operationTimeoutCnt;
    }

    @Override
    public int getOperationTimeoutCnt() {
        return this.operationTimeoutCnt;
    }

    @Override
    public void clearOperationTimeoutCnt() {
        this.operationTimeoutCnt = 0;
        this.setAutoMode(false);
    }

    @Override
    public int betweenDistance(IRoomPlayer other) {
        return Location.getDistance(this.getLat(), this.getLng(), other.getLat(), other.getLng());
    }

    @Override
    public double getLat() {
        if (-1 == this.playerUid) {
            return 0;
        }
        Player player = PlayerManager.I.getPlayer(this.playerUid);
        if (null == player) {
            return 0;
        }
        return player.getLat();
    }

    @Override
    public double getLng() {
        if (-1 == this.playerUid) {
            return 0;
        }
        Player player = PlayerManager.I.getPlayer(this.playerUid);
        if (null == player) {
            return 0;
        }
        return player.getLng();
    }

    @Override
    public void clear() {
        this.operationTimeoutCnt = 0;
        this.isAutoMode = false;
        this.score.clear();
        this.over = false;
    }

    @Override
    public void clearByArenaOver() {

    }

    @Override
    public long getUid() {
        return this.playerUid;
    }

    @Override
    public void send(int commandId, Object message) {
        if (EState.ONLINE != this.state.get()) {
            return;
        }
        IPlayer player = PlayerManager.I.getOnlinePlayer(this.playerUid);
        if (null != player) {
            player.send(commandId, message);
        }
    }

    @Override
    public IPlayer getPlayer() {
        if (-1 == this.playerUid) {
            return null;
        }
        return PlayerManager.I.getPlayer(this.playerUid);
    }

    @Override
    public void setPlayer(IPlayer player) {
        this.playerUid = null == player ? -1 : player.getUid();
    }

    @Override
    public boolean isOffline() {
        return EState.ONLINE != this.state.get();
    }

    @Override
    public EState getState() {
        return this.state.get();
    }

    @Override
    public void setRoom(IRoom room) {
        this.roomUid = null == room ? -1 : room.getRoomUid();
        this.roomId = null == room ? -1 : room.getRoomId();
        if (-1 != this.playerUid) {
            IPlayer player = PlayerManager.I.getPlayer(this.playerUid);
            if (null == room) {
                player.changeRoomId(-1, -1);
            } else {
                player.changeRoomId(room.getRoomId(), room.getGameType());
            }
        }
    }

    @Override
    public long getRoomUid() {
        return roomUid;
    }

    @Override
    public int getRoomId() {
        return roomId;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void setGuest(boolean guest) {
        this.guest = guest;
    }

    @Override
    public boolean isGuest() {
        return this.guest;
    }

    @Override
    public void setOver(boolean over) {
        this.over = over;
    }

    @Override
    public boolean isOver() {
        return this.over;
    }

    public boolean isAutoMode() {
        if (this.getOperationTimeoutCnt() == 2) {
            return true;
        }
        return isAutoMode;
    }

    public void setAutoMode(boolean autoMode) {
        isAutoMode = autoMode;
    }

    @Override
    public String toString() {
        return String.format("RoomPlayer[PlayerUid:%s State:%s Index:%d Guess:%s]", this.playerUid, this.state.get(),
                index, this.guest);
    }

    @Override
    public IRoomPlayerHelper getRoomPlayerHelper() {
        if (null != this.roomPlayerHelper) {
            return this.roomPlayerHelper;
        }
        synchronized (this) {
            if (null != this.roomPlayerHelper) {
                return roomPlayerHelper;
            }
            this.roomPlayerHelper = RoomPlayerHelperFactory.createRoomPlayerHelper(this, gameType);
        }
        return this.roomPlayerHelper;
    }
    
    @Override
    public int getGameType() {
        return gameType;
    }
}
