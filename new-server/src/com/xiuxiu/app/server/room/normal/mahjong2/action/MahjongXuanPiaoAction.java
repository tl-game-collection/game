package com.xiuxiu.app.server.room.normal.mahjong2.action;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongXuanPiao;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IXuanPiao;

import java.util.HashMap;
import java.util.Map;

public class MahjongXuanPiaoAction extends BaseMahjongAction {
    protected HashMap<Long, Integer> allPlayer = new HashMap<>();
    protected int cnt = 0;

    public MahjongXuanPiaoAction(IRoom room, long timeout) {
        super(room, EActionOp.XUAN_ZENG, timeout);
    }

    public void addPlayer(IMahjongPlayer player) {
        this.allPlayer.put(player.getUid(), -1);
    }

    public ErrorCode xuanPiao(IMahjongPlayer player, int value) {
        if (-1 != this.allPlayer.getOrDefault(player.getUid(), -1)) {
            return ErrorCode.PLAYER_ALREADY_OPERATE;
        }
        ((IXuanPiao) player).setPiao(value);
        this.allPlayer.put(player.getUid(), value);
        ++this.cnt;
        ((IMahjongXuanPiao) this.room).doSendXuanPiaoInfo(player, value);
        return ErrorCode.OK;
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout) {
            for (Map.Entry<Long, Integer> entry : this.allPlayer.entrySet()) {
                if (-1 != entry.getValue()) {
                    continue;
                }
                IXuanPiao player = (IXuanPiao) this.room.getRoomPlayer(entry.getKey());
                player.setPiao(0);
                ((IMahjongXuanPiao) this.room).doSendXuanPiaoInfo((IMahjongPlayer) player, 0);
            }
            this.cnt = this.allPlayer.size();
        }
        if (this.cnt >= this.allPlayer.size()) {
            ((IMahjongXuanPiao) this.room).doSendEndXuanPiao();
            ((IMahjongXuanPiao) this.room).endXuanPiao();
            return true;
        }
        return false;
    }

    @Override
    protected void doRecover() {
        for (Map.Entry<Long, Integer> entry : this.allPlayer.entrySet()) {
            ((IMahjongXuanPiao) this.room).doSendBeginXuanPiao((IMahjongPlayer) this.room.getRoomPlayer(entry.getKey()));
        }
    }

    @Override
    public void online(IRoomPlayer player) {
        if (this.allPlayer.containsKey(player.getUid())) {
            ((IMahjongXuanPiao) this.room).doSendBeginXuanPiao((IMahjongPlayer) player);
        }
    }
}
