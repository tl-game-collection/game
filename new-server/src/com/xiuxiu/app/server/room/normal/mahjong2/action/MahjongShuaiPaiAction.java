package com.xiuxiu.app.server.room.normal.mahjong2.action;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong2.IMahjongShuaiPai;
import com.xiuxiu.app.server.room.player.mahjong2.IMahjongPlayer;

import java.util.HashMap;
import java.util.Map;

public class MahjongShuaiPaiAction extends BaseMahjongAction {
    protected HashMap<Long, Integer> allPlayer = new HashMap<>();
    protected int cnt = 0;

    public MahjongShuaiPaiAction(IRoom room, long timeout) {
        super(room, EActionOp.SHUAI_PAI, timeout);
    }

    public void addPlayer(IMahjongPlayer player) {
        this.allPlayer.put(player.getUid(), -1);
    }

    public ErrorCode shuaiPai(IMahjongPlayer player, int card) {
        if (-1 != this.allPlayer.getOrDefault(player.getUid(), -1)) {
            return ErrorCode.PLAYER_ALREADY_OPERATE;
        }
        if (0 != card) {
            byte card1 = (byte) (card & 0x3F);
            byte card2 = (byte) ((card >> 6) & 0x3F);
            byte card3 = (byte) ((card >> 12) & 0x3F);

            if (!player.hasHandCard(card1, card2, card3)) {
                return ErrorCode.REQUEST_INVALID_DATA;
            }
            player.delHandCard(card1);
            player.delHandCard(card2);
            player.delHandCard(card3);
            player.addCPGWithAnyThree(player.getIndex(), card1, card2, card3);
        }

        this.allPlayer.put(player.getUid(), card);
        ++this.cnt;
        ((IMahjongShuaiPai) this.room).doSendShuaiPaiInfo(player, card, 3);
        return ErrorCode.OK;
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout || this.cnt >= this.allPlayer.size()) {
            ((IMahjongShuaiPai) this.room).doSendEndShuaiPai();
            ((IMahjongShuaiPai) this.room).endShuaiPai();
            return true;
        }
        return false;
    }

    @Override
    protected void doRecover() {
        for (Map.Entry<Long, Integer> entry : this.allPlayer.entrySet()) {
            ((IMahjongShuaiPai) this.room).doSendBeginShuaiPai((IMahjongPlayer) this.room.getRoomPlayer(entry.getKey()), entry.getValue(), 3);
        }
    }

    @Override
    public void online(IRoomPlayer player) {
        if (this.allPlayer.containsKey(player.getUid())) {
            ((IMahjongShuaiPai) this.room).doSendBeginShuaiPai((IMahjongPlayer) player, this.allPlayer.get(player.getUid()), 3);
        }
    }
}
