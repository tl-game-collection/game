package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

public class PassRecordAction extends RecordAction {
    public PassRecordAction(long playerUid) {
        super(EActionOp.PASS, playerUid);
    }
}
