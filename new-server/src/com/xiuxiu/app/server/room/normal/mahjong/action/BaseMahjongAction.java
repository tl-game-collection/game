package com.xiuxiu.app.server.room.normal.mahjong.action;

import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.BaseAction;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong.IMahjongRoom;
import com.xiuxiu.app.server.room.player.mahjong.MahjongPlayer;

public abstract class BaseMahjongAction extends BaseAction {
    protected final MahjongPlayer roomPlayer;

    public BaseMahjongAction(IMahjongRoom room, EActionOp op, MahjongPlayer roomPlayer, long timeout) {
        super(room, op, timeout);
        this.roomPlayer = roomPlayer;
    }

    public MahjongPlayer getRoomPlayer() {
        return this.roomPlayer;
    }

    @Override
    public void online(IRoomPlayer player) {
    }

    @Override
    public void offline(IRoomPlayer player) {
    }

    @Override
    protected void operationTimeout() {
        if (null != this.roomPlayer) {
            this.roomPlayer.operationTimeout();
        }
    }
}
