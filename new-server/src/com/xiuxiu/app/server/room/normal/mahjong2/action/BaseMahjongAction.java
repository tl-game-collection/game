package com.xiuxiu.app.server.room.normal.mahjong2.action;

import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.BaseAction;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

public abstract class BaseMahjongAction extends BaseAction {
    protected IMahjongPlayer player;

    public BaseMahjongAction(IRoom room, EActionOp op, long timeout) {
        this(room, null, op, timeout);
    }

    public BaseMahjongAction(IRoom room, IMahjongPlayer player, EActionOp op, long timeout) {
        super(room, op, timeout);
        this.player = player;
    }

    public IMahjongPlayer getPlayer() {
        return this.player;
    }

    @Override
    public void online(IRoomPlayer player) {
    }

    @Override
    public void offline(IRoomPlayer player) {
    }

    @Override
    protected void operationTimeout() {
        if (null != this.player) {
            this.player.operationTimeout();
        }
    }
}
