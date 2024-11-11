package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.HashMap;

public class GDyMoCardRecordAction extends RecordAction {

    protected HashMap<Long, Byte> moCards = new HashMap<>();

    public GDyMoCardRecordAction() {
        super(EActionOp.MO_CARD, -1);
    }

    public void addMoCardPlayer(long playerUid, Byte card) {
        this.moCards.put(playerUid, card);
    }

    public HashMap<Long, Byte> getMoCards() {
        return this.moCards;
    }
}
