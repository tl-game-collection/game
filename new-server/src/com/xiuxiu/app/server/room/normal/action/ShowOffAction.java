package com.xiuxiu.app.server.room.normal.action;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.core.ds.ConcurrentHashSet;

public class ShowOffAction extends BaseAction {
    protected ConcurrentHashSet<Long> allPlayer = new ConcurrentHashSet<>();

    public ShowOffAction(IRoom room, long timeout) {
        super(room, EActionOp.SHOW_OFF, timeout);
    }

    public void addPlayer(long playerUid) {
        this.allPlayer.add(playerUid);
    }

    public ErrorCode showOff(IRoomPlayer player) {
        if (this.allPlayer.remove(player.getUid())) {
            return this.room.onShowOff(player);
        }
        return ErrorCode.ROOM_ALREADY_SHOW_OFF;
    }

    @Override
    public boolean action(boolean timeout) {
        boolean over = timeout || this.allPlayer.isEmpty();
        if (over) {
            this.room.doShowOffOver();
        }
        return over;
    }

    @Override
    protected void doRecover() {

    }

    @Override
    protected void operationTimeout() {

    }

    @Override
    public void online(IRoomPlayer player) {

    }

    @Override
    public void offline(IRoomPlayer player) {

    }
}
