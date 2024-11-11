package com.xiuxiu.app.server.room.record.mahjong2;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

public class FumbleRecordAction extends RecordAction {
    protected byte card;

    public FumbleRecordAction(long playerUid, byte card) {
        super(EActionOp.FUMBLE, playerUid);
        this.card = card;
    }

    public byte getCard() {
        return card;
    }

    public void setCard(byte card) {
        this.card = card;
    }
}
