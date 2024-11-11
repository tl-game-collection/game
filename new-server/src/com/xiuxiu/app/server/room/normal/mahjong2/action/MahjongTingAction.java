package com.xiuxiu.app.server.room.normal.mahjong2.action;

import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongBright;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongRoom;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

public class MahjongTingAction extends MahjongTakeAction {
    protected EActionOp op = EActionOp.TING;

    public MahjongTingAction(IRoom room, IMahjongPlayer player, long timeout) {
        super(room, player, timeout);
        this.setOp(EActionOp.TING);
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout) {
            this.op = EActionOp.TING;
            this.initAutoTakeCardParam();
        }
        switch (this.op) {
            case TING:
                ((IMahjongRoom) this.room).onTing(this.player, timeout, this.param);
                break;
            case TAKE:
                ((IMahjongRoom) this.room).onTake(this.player, timeout, this.param);
                break;
            case BAR:
            case MUST_BAR:
                ((IMahjongRoom) this.room).onBar(this.player, this.player, this.param);
                break;
            case HU:
                ((IMahjongRoom) this.room).onHu(this.player, this.player, (Byte) this.param[0]);
                break;
            case BRIGHT:
                ((IMahjongBright) this.room).onBright(this.player, this.param);
                break;
        }
        return true;
    }

    @Override
    protected void doRecover() {
        ((IMahjongRoom) this.room).doSendCanTing(this.getPlayer(), true);
    }

    @Override
    public void online(IRoomPlayer player) {
        if (this.player.getUid() != player.getUid()) {
            return;
        }
        ((IMahjongRoom) this.room).doSendCanTing(this.getPlayer(), false);
    }

    @Override
    public void setParam(Object... param) {
        this.param = param;
    }

    @Override
    public Object[] getParam() {
        return this.param;
    }
}


