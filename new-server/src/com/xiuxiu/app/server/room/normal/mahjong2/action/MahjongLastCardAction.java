package com.xiuxiu.app.server.room.normal.mahjong2.action;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.BaseMahjongRoom;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongLastCard;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

public class MahjongLastCardAction extends BaseMahjongAction {
    public int startIndex = -1;
    public int curIndex = -1;
    public byte card;
    public long expire = -1;
    public int cnt = 0;
    public boolean finish = false;

    public MahjongLastCardAction(IRoom room) {
        super(room, null, EActionOp.LAST_CARD, -1);
    }

    public void setStartIndex(int index, byte card) {
        this.startIndex = index;
        this.curIndex = index;
        this.card = card;
    }

    public void start() {
        this.expire = System.currentTimeMillis();
    }

    public ErrorCode select(IMahjongPlayer player, boolean select) {
        if (this.curIndex != player.getIndex()) {
            Logs.ROOM.warn("%s 最后一张牌选择错误, 不该你选择: %s, 当前选择的人是:%d", this.room, player, this.startIndex);
            return ErrorCode.REQUEST_OPERATE_ERROR;
        }
        if (select) {
            this.finish = true;
            ((IMahjongLastCard) this.room).endLastCard(player, card);
        } else {
            this.next();
        }
        return ErrorCode.OK;
    }

    public void next() {
        this.curIndex = (this.curIndex + 1) % this.room.getCurPlayerCnt();
        if (this.curIndex == this.startIndex) {
            this.finish = true;
            ((IMahjongLastCard) this.room).endLastCard(null, card);
            return;
        }
        this.expire = System.currentTimeMillis() + BaseMahjongRoom.LAST_CARD_SELECT_TIMEOUT;
        IRoomPlayer  player = this.room.getRoomPlayer(this.curIndex);
        if(player==null)
        	player = this.room.getNextRoomPlayer(this.curIndex);
        ((IMahjongLastCard) this.room).doSendBeginLastCard((IMahjongPlayer)player);
    }

    @Override
    public boolean action(boolean timeout) {
        if (this.finish) {
            return true;
        }
        if (timeout) {
            this.next();
        }
        return false;
    }


    @Override
    public boolean canAction(long curTime) {
        if (this.finish) {
            return true;
        }
        return curTime >= this.expire;
    }

    @Override
    protected void doRecover() {
        ((IMahjongLastCard) this.room).doSendBeginLastCard((IMahjongPlayer) this.room.getRoomPlayer(this.startIndex));
    }

    @Override
    public void online(IRoomPlayer player) {
        if (player.getIndex() == this.startIndex) {
            ((IMahjongLastCard) this.room).doSendBeginLastCard((IMahjongPlayer) this.room.getRoomPlayer(this.startIndex));
        }
    }


}
