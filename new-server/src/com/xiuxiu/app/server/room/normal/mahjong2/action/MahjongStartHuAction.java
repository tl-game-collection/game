package com.xiuxiu.app.server.room.normal.mahjong2.action;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongRoom;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongStartHu;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;
import com.xiuxiu.app.server.room.player.mahjong2.IStartHu;

import java.util.HashMap;
import java.util.Map;

public class MahjongStartHuAction extends BaseMahjongAction {
    protected HashMap<Long, Integer> allPlayer = new HashMap<>();
    protected int cnt = 0;

    public MahjongStartHuAction(IRoom room, long timeout) {
        super(room, EActionOp.XUAN_ZENG, timeout);
    }

    public void addPlayer(IMahjongPlayer player) {
        this.allPlayer.put(player.getUid(), -1);
    }

    public ErrorCode startHu(IMahjongPlayer player, boolean hu) {
        if (-1 != this.allPlayer.getOrDefault(player.getUid(), -1)) {
            return ErrorCode.PLAYER_ALREADY_OPERATE;
        }
        if (hu) {
            ((IMahjongRoom) this.room).onHu(player, player, (byte) -1);
        }
        ((IStartHu) player).setHu(hu);
        this.allPlayer.put(player.getUid(), hu ? 1 : 0);
        ++this.cnt;
        ((IMahjongStartHu) this.room).doSendStartHuInfo(player, hu);
        return ErrorCode.OK;
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout) {
            for (Map.Entry<Long, Integer> entry : this.allPlayer.entrySet()) {
                if (-1 != entry.getValue()) {
                    continue;
                }
                IStartHu player = (IStartHu) this.room.getRoomPlayer(entry.getKey());
                player.setHu(true);
                ((IMahjongStartHu) this.room).doSendStartHuInfo((IMahjongPlayer) player, false);
            }
            this.cnt = this.allPlayer.size();
        }
        if (this.cnt >= this.allPlayer.size()) {
            ((IMahjongStartHu) this.room).doSendEndStartHu();
            ((IMahjongStartHu) this.room).endStartHu(true);
            return true;
        }
        return false;
    }

    @Override
    protected void doRecover() {
        for (Map.Entry<Long, Integer> entry : this.allPlayer.entrySet()) {
            ((IMahjongStartHu) this.room).doSendBeginStartHu((IMahjongPlayer) this.room.getRoomPlayer(entry.getKey()), null);
        }
    }

    @Override
    public void online(IRoomPlayer player) {
        if (this.allPlayer.containsKey(player.getUid())) {
            ((IMahjongStartHu) this.room).doSendBeginStartHu((IMahjongPlayer) player, null);
        }
    }
}
