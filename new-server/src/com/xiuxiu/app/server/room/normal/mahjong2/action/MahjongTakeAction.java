package com.xiuxiu.app.server.room.normal.mahjong2.action;

import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongBright;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongRoom;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

public class MahjongTakeAction extends BaseMahjongAction {
    protected EActionOp op = EActionOp.TAKE;
    protected Object[] param;

    public MahjongTakeAction(IRoom room, IMahjongPlayer player, long timeout) {
        super(room, player, EActionOp.TAKE, timeout);
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout) {
            this.op = EActionOp.TAKE;
            this.initAutoTakeCardParam();
        }
        switch (this.op) {
            case TAKE:
                ((IMahjongRoom) this.room).onTake(this.player, timeout ? true : false, this.param);
                break;
            case BAR:
                ((IMahjongRoom) this.room).onBar(this.player, this.player, this.param);
                break;
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

    protected void initAutoTakeCardParam() {
        byte dCard = (byte) param[0];
        byte card = (byte) param[0];
        card = ((IMahjongRoom) this.room).getCanTakeHandCard(player, card);
        byte isLast = 0;
        byte index = -1;
        byte outputCardIndex = -1;
        int length = 0;

        boolean fumble = ((IMahjongRoom) this.room).isCurAction(this.player, EActionOp.FUMBLE);

        if (!fumble || dCard != card) {
            outputCardIndex = player.getHandCardIndex(card);
            if (fumble) {
                index = (byte) (outputCardIndex + 1);
            }
        } else {
            isLast = 1;
        }

        this.setParam(card, isLast, index, outputCardIndex, length);
    }

    @Override
    protected void doRecover() {
        ((IMahjongRoom) this.room).doSendCanTake(this.getPlayer(), true);
    }

    @Override
    public void online(IRoomPlayer player) {
        if (this.player.getUid() != player.getUid()) {
            return;
        }
        ((IMahjongRoom) this.room).doSendCanTake(this.getPlayer(), false);
    }

    public EActionOp getOp() {
        return op;
    }

    public void setOp(EActionOp op) {
        this.op = op;
    }

    public void setParam(Object... param) {
        this.param = param;
    }

    public Object[] getParam() {
        return this.param;
    }
    
    public void setTimeout(long timeout) {
        this.timeout = timeout;
        this.sourceTimeout = timeout;
    }
}
