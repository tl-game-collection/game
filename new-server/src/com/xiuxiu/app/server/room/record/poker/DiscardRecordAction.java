package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

public class DiscardRecordAction extends RecordAction {
    public DiscardRecordAction(long playerUid) {
        super(EActionOp.DISCARD, playerUid);
    }
}
