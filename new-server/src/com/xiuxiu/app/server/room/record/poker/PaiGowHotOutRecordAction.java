package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

public class PaiGowHotOutRecordAction extends RecordAction {
    private boolean out;
    private boolean five;

    public PaiGowHotOutRecordAction(long playerUid, boolean out, boolean five) {
        super(EActionOp.HOT_OUT, playerUid);
        this.out = out;
        this.five = five;
    }

    public boolean isOut() {
        return out;
    }

    public boolean isFive() {
        return five;
    }
}

