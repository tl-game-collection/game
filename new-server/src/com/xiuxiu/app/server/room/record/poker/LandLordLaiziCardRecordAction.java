package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.List;

public class LandLordLaiziCardRecordAction extends RecordAction {

    protected List<Byte> laiziCards;

    public LandLordLaiziCardRecordAction(List<Byte> cards) {
        super(EActionOp.SHOW_LAIZI_CARD, -1);
        this.laiziCards = cards;
    }

    public void setCards(List<Byte> cards) {
        this.laiziCards = cards;
    }

    public List<Byte> getCards() {
        return laiziCards;
    }
}
