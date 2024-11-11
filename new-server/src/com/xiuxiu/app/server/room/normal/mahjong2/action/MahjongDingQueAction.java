package com.xiuxiu.app.server.room.normal.mahjong2.action;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongDingQue;
import com.xiuxiu.app.server.room.player.mahjong2.IDingQue;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

import java.util.HashMap;
import java.util.Map;

public class MahjongDingQueAction extends BaseMahjongAction {
    protected HashMap<Long, Integer> allPlayer = new HashMap<>();
    protected int cnt = 0;

    public MahjongDingQueAction(IRoom room, long timeout) {
        super(room, EActionOp.DING_QUE, timeout);
    }

    public void addPlayer(IMahjongPlayer player) {
        this.allPlayer.put(player.getUid(), -1);
    }

    public ErrorCode dingQue(IMahjongPlayer player, int color) {
        if (-1 != this.allPlayer.getOrDefault(player.getUid(), -1)) {
            return ErrorCode.PLAYER_ALREADY_OPERATE;
        }
        ((IDingQue) player).setQue(color);
        this.allPlayer.put(player.getUid(), color);
        ++this.cnt;
        ((IMahjongDingQue) this.room).doSendDingQueInfo(player, color);
        return ErrorCode.OK;
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout) {
            for (Map.Entry<Long, Integer> entry : this.allPlayer.entrySet()) {
                if (-1 != entry.getValue()) {
                    continue;
                }
                IDingQue player = (IDingQue) this.room.getRoomPlayer(entry.getKey());
                int color = player.setQue(-1);
                ((IMahjongDingQue) this.room).doSendDingQueInfo((IMahjongPlayer) player, color);
            }
            this.cnt = this.allPlayer.size();
        }
        if (this.cnt >= this.allPlayer.size()) {
            ((IMahjongDingQue) this.room).doSendEndDingQue();
            ((IMahjongDingQue) this.room).endDingQue();
            return true;
        }
        return false;
    }

    @Override
    protected void doRecover() {
        for (Map.Entry<Long, Integer> entry : this.allPlayer.entrySet()) {
            ((IMahjongDingQue) this.room).doSendBeginDingQue((IMahjongPlayer) this.room.getRoomPlayer(entry.getKey()));
        }
    }

    @Override
    public void online(IRoomPlayer player) {
        if (this.allPlayer.containsKey(player.getUid())) {
            ((IMahjongDingQue) this.room).doSendBeginDingQue((IMahjongPlayer) player);
        }
    }
}
