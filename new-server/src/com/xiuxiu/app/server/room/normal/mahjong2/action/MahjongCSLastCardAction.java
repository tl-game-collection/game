package com.xiuxiu.app.server.room.normal.mahjong2.action;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.BaseMahjongRoom;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongLastCard;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

public class MahjongCSLastCardAction extends MahjongLastCardAction {
    private boolean flag=false;
    public MahjongCSLastCardAction(IRoom room) {
        super(room);
    }

    @Override
    public boolean action(boolean timeout) {
        if (this.finish) {
            return true;
        }
        if (timeout) {
            if(!flag){
                this.expire = System.currentTimeMillis() + BaseMahjongRoom.LAST_CARD_SELECT_TIMEOUT;
                ((IMahjongLastCard) this.room).doSendBeginLastCard((IMahjongPlayer) this.room.getRoomPlayer(this.curIndex));
                flag=true;
            }else{
            this.next();
            }
        }
        return false;
    }


}
