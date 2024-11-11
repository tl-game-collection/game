package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.List;

public class LandLordLastCardRecordAction extends RecordAction {
    protected List<Byte> cards;

    public LandLordLastCardRecordAction(long playerUid, List<Byte> cards) {
        super(EActionOp.SHOW_LAST_CARD, playerUid);
        this.cards = cards;
    }

    public void setLastCard(List<Byte> cards) {
        this.cards = cards;
    }

    public List<Byte> getLastCard() {
        return cards;
    }

}
