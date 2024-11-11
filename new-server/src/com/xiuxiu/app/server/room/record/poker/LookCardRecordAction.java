package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.ArrayList;
import java.util.List;

public class LookCardRecordAction extends RecordAction {
    private List<Byte> card = new ArrayList<>();

    public LookCardRecordAction(long playerUid, List<Byte> card) {
        super(EActionOp.LOOK_CARD, playerUid);
        this.card.addAll(card);
    }

    public List<Byte> getCard() {
        return card;
    }

    public void setCard(List<Byte> card) {
        this.card = card;
    }
}
