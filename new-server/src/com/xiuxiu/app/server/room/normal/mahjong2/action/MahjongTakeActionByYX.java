package com.xiuxiu.app.server.room.normal.mahjong2.action;

import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongBright;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongRoom;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

public class MahjongTakeActionByYX extends MahjongTakeAction {
    public MahjongTakeActionByYX(IRoom room, IMahjongPlayer player, long timeout) {
        super(room, player, timeout);
    }

    @Override
    public boolean action(boolean timeout) {
        switch (this.op) {
            case TAKE:
                if (timeout) {
                    this.initAutoTakeCardParam();
                }
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
}
