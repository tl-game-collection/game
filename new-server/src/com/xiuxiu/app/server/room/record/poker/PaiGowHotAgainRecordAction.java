package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

public class PaiGowHotAgainRecordAction extends RecordAction {
    private boolean again;

    public PaiGowHotAgainRecordAction(long playerUid, boolean again) {
        super(EActionOp.HOT_AGAIN, playerUid);
        this.again = again;
    }

    public boolean isAgain() {
        return again;
    }
}

