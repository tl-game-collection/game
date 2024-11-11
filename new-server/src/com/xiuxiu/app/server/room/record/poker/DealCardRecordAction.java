package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.ArrayList;
import java.util.List;

public class DealCardRecordAction extends RecordAction {
    private List<Byte> cards = new ArrayList<>();

    public DealCardRecordAction(long playerUid, List<Byte> cards) {
        super(EActionOp.DEAL_CARD, playerUid);
        this.cards.addAll(cards);
    }

    public List<Byte> getCards() {
        return cards;
    }

    public void setCards(List<Byte> cards) {
        this.cards = cards;
    }
}
