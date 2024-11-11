package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.List;

public class LandLordShowAllCardRecordAction extends RecordAction {
    protected List<Byte> cards;

    public LandLordShowAllCardRecordAction(long playerUid, List<Byte> cards) {
        super(EActionOp.SHOW_ALL_CARD, playerUid);
        this.cards = cards;
    }

    public void setCards(List<Byte> cards) {
        this.cards = cards;
    }

    public List<Byte> getCards() {
        return cards;
    }
}
