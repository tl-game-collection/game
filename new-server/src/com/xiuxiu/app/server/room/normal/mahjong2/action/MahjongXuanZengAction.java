package com.xiuxiu.app.server.room.normal.mahjong2.action;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongXuanZeng;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IXuanZeng;

import java.util.HashMap;
import java.util.Map;

public class MahjongXuanZengAction extends BaseMahjongAction {
    protected HashMap<Long, Integer> allPlayer = new HashMap<>();
    protected int cnt = 0;

    public MahjongXuanZengAction(IRoom room, long timeout) {
        super(room, EActionOp.XUAN_ZENG, timeout);
    }

    public void addPlayer(IMahjongPlayer player) {
        this.allPlayer.put(player.getUid(), -1);
    }

    public ErrorCode xuanZeng(IMahjongPlayer player, int value) {
        if (-1 != this.allPlayer.getOrDefault(player.getUid(), -1)) {
            return ErrorCode.PLAYER_ALREADY_OPERATE;
        }
        ((IXuanZeng) player).setZeng(value);
        this.allPlayer.put(player.getUid(), value);
        ++this.cnt;
        ((IMahjongXuanZeng) this.room).doSendXuanZengInfo(player, value);
        return ErrorCode.OK;
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout) {
            for (Map.Entry<Long, Integer> entry : this.allPlayer.entrySet()) {
                if (-1 != entry.getValue()) {
                    continue;
                }
                IXuanZeng player = (IXuanZeng) this.room.getRoomPlayer(entry.getKey());
                player.setZeng(0);
                ((IMahjongXuanZeng) this.room).doSendXuanZengInfo((IMahjongPlayer) player, 0);
            }
            this.cnt = this.allPlayer.size();
        }
        if (this.cnt >= this.allPlayer.size()) {
            ((IMahjongXuanZeng) this.room).doSendEndXuanZeng();
            ((IMahjongXuanZeng) this.room).endXuanZeng();
            return true;
        }
        return false;
    }

    @Override
    protected void doRecover() {
        for (Map.Entry<Long, Integer> entry : this.allPlayer.entrySet()) {
            ((IMahjongXuanZeng) this.room).doSendBeginXuanZeng((IMahjongPlayer) this.room.getRoomPlayer(entry.getKey()));
        }
    }

    @Override
    public void online(IRoomPlayer player) {
        if (this.allPlayer.containsKey(player.getUid())) {
            ((IMahjongXuanZeng) this.room).doSendBeginXuanZeng((IMahjongPlayer) player);
        }
    }
}
