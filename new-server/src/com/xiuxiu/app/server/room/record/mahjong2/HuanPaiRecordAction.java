package com.xiuxiu.app.server.room.record.mahjong2;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.HashMap;
import java.util.List;

public class HuanPaiRecordAction extends RecordAction {
    protected HashMap<Long, List<Byte>> allHandCard = new HashMap<>();

    public HuanPaiRecordAction() {
        super(EActionOp.HUAN_PAI, -1);
    }

    public void addHuanPai(long playerUid, List<Byte> handCard) {
        this.allHandCard.put(playerUid, handCard);
    }

    public HashMap<Long, List<Byte>> getAllHandCard() {
        return allHandCard;
    }

    public void setAllHandCard(HashMap<Long, List<Byte>> allHandCard) {
        this.allHandCard = allHandCard;
    }
}
