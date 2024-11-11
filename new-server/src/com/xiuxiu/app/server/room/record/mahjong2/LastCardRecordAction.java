package com.xiuxiu.app.server.room.record.mahjong2;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

public class LastCardRecordAction extends RecordAction {
    private byte card;

    public LastCardRecordAction(long playerUid, byte card) {
        super(EActionOp.LAST_CARD, playerUid);
        this.card = card;
    }

    public byte getCard() {
        return card;
    }

    public void setCard(byte card) {
        this.card = card;
    }
}
