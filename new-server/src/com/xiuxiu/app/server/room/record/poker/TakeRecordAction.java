package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.List;

public class TakeRecordAction extends RecordAction {
    protected List<Byte> card;

    public TakeRecordAction(long playerUid, List<Byte> card) {
        super(EActionOp.TAKE, playerUid);
        this.card = card;
    }

    public List<Byte> getCard() {
        return card;
    }

    public void setCard(List<Byte> card) {
        this.card = card;
    }
}
