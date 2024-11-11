package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

public class CheckRecordAction extends RecordAction {
    public CheckRecordAction(long playerUid) {
        super(EActionOp.CHECK, playerUid);
    }
}
