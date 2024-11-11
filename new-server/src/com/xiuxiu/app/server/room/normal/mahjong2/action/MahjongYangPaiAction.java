package com.xiuxiu.app.server.room.normal.mahjong2.action;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongYangPai;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MahjongYangPaiAction extends BaseMahjongAction {
    private Map<Long, List<Byte>> players = new HashMap<>();

    public MahjongYangPaiAction(IRoom room, long timeout) {
        super(room, EActionOp.YANG_PAI, timeout);
    }

    public void addPlayer(IMahjongPlayer player) {
        this.players.put(player.getUid(),null);
    }

    public ErrorCode yangPai(IMahjongPlayer player, List<Byte> cards) {
        if (this.players.get(player.getUid()) != null) {
            return ErrorCode.PLAYER_ALREADY_OPERATE;
        }
        this.players.put(player.getUid(), new ArrayList<>(cards));
        ((IMahjongYangPai) this.room).doSendYangPaiInfo(player, cards);
        return ErrorCode.OK;
    }

    @Override
    public boolean action(boolean timeout) {
        boolean over = timeout;
        if (!over) {
            over = true;
            for (Map.Entry<Long, List<Byte>> entry : this.players.entrySet()) {
                if (entry.getValue() == null) {
                    over = false;
                    break;
                }
            }
        }
        if (over) {
            ((IMahjongYangPai) this.room).endYangPai();
            return true;
        }
        return false;
    }

    @Override
    protected void doRecover() {
        for (Map.Entry<Long, List<Byte>> entry : this.players.entrySet()) {
            ((IMahjongYangPai) this.room).doSendBeginYangPai((IMahjongPlayer) this.room.getRoomPlayer(entry.getKey()));
        }
    }

    @Override
    public void online(IRoomPlayer player) {
        if (this.players.containsKey(player.getUid())) {
            ((IMahjongYangPai) this.room).doSendBeginYangPai((IMahjongPlayer) player);
        }
    }
}
