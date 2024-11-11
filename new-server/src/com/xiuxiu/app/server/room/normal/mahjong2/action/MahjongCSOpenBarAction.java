package com.xiuxiu.app.server.room.normal.mahjong2.action;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongCSOpenBar;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

public class MahjongCSOpenBarAction extends BaseMahjongAction {
    private boolean select = false;
    public MahjongCSOpenBarAction (IRoom room, IMahjongPlayer player, long timeout) {
        super(room, player, EActionOp.OPEN_BAR, timeout);
    }

    public ErrorCode select(boolean value) {
        this.select = value;
        return ErrorCode.OK;
    }

    @Override
    public boolean action(boolean timeout) {
        ((IMahjongCSOpenBar) this.room).endCSOpenCard(this.player, this.select);
        return true;
    }

    @Override
    protected void doRecover() {
        ((IMahjongCSOpenBar) this.room).doSendBeginCSOpenCard(this.player);
    }

    @Override
    public void online(IRoomPlayer player) {
        if (player.getUid() == this.player.getUid()) {
            ((IMahjongCSOpenBar) this.room).doSendBeginCSOpenCard(this.player);
        }
    }
}
