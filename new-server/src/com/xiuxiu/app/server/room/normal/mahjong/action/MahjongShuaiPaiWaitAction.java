package com.xiuxiu.app.server.room.normal.mahjong.action;

import com.xiuxiu.algorithm.mahjong.MahjongUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfBeginShuaiPai;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong.IMahjongRoom;
import com.xiuxiu.app.server.room.normal.mahjong.MahjongRoom;
import com.xiuxiu.app.server.room.player.mahjong.MahjongPlayer;
import com.xiuxiu.app.server.room.record.mahjong.MahjongRecord;
import com.xiuxiu.app.server.room.record.mahjong.ShuaiPaiRecordAction;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MahjongShuaiPaiWaitAction extends BaseMahjongAction {
    private ConcurrentHashMap<Long, List<Byte>> playerOp = new ConcurrentHashMap<>();
    private ShuaiPaiRecordAction shuaiPaiRecordAction;
    private int cnt = 0;
    private byte[] tempHandCard = new byte[MahjongUtil.MJ_CARD_KINDS];

    public MahjongShuaiPaiWaitAction(IMahjongRoom room, MahjongPlayer roomPlayer, long timeout) {
        super(room, EActionOp.SHUAI_PAI, roomPlayer, timeout);
        this.shuaiPaiRecordAction = ((MahjongRecord) room.getRecord()).addShuaiPaiAction();
    }

    public void addPlayer(long uid) {
        this.playerOp.put(uid, Collections.EMPTY_LIST);
    }

    public ErrorCode shuaiPai(long uid, List<Byte> card) {
        if (3 != card.size()) {
            return ErrorCode.REQUEST_INVALID_DATA;
        }
        List<Byte> v = this.playerOp.get(uid);
        if (Collections.EMPTY_LIST != v) {
            return ErrorCode.PLAYER_ALREADY_OPERATE;
        }
        MahjongPlayer player = (MahjongPlayer) this.room.getRoomPlayer(uid);
        if (null == player || player.isGuest()) {
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        byte[] handCard = player.getHandCard();
        for (int i = 0; i < MahjongUtil.MJ_CARD_KINDS; ++i) {
            this.tempHandCard[i] = handCard[i];
        }
        for (Byte c : card) {
            --this.tempHandCard[c];
            if (this.tempHandCard[c] < 0) {
                return ErrorCode.REQUEST_INVALID_DATA;
            }
        }
        for (Byte c : card) {
            player.delHandCard(c, 1);
            player.addShuaiPai(c);
        }
        this.playerOp.put(uid, card);
        this.shuaiPaiRecordAction.addShuaiPai(uid, card);
        ++this.cnt;
        return ErrorCode.OK;
    }

    @Override
    public boolean action(boolean timeout) {
        if (timeout || this.cnt >= this.playerOp.size()) {
            this.room.broadcast2Client(CommandId.CLI_NTF_MAHJONG_END_SHUAI_PAI, null);
            ((MahjongRoom) this.room).endShuaiPai();
            return true;
        }
        return false;
    }

    @Override
    protected void doRecover() {
        Iterator<Map.Entry<Long, List<Byte>>> it = this.playerOp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, List<Byte>> entry = it.next();
            PCLIMahjongNtfBeginShuaiPai info = new PCLIMahjongNtfBeginShuaiPai();
            List<Byte> card = entry.getValue();
            if (Collections.EMPTY_LIST != card) {
                info.card.addAll(card);
            }
            this.room.getRoomPlayer(entry.getKey()).send(CommandId.CLI_NTF_MAHJONG_BEGIN_SHUAI_PAI, info);
        }
    }

    @Override
    public void online(IRoomPlayer player) {
        PCLIMahjongNtfBeginShuaiPai info = new PCLIMahjongNtfBeginShuaiPai();
        List<Byte> card = this.playerOp.get(player.getUid());
        if (Collections.EMPTY_LIST != card) {
            info.card.addAll(card);
        }
        player.send(CommandId.CLI_NTF_MAHJONG_BEGIN_SHUAI_PAI, info);
    }
}
