package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

public class AllInRecordAction extends RecordAction {
    private int note;
    public AllInRecordAction(long playerUid,int note) {
        super(EActionOp.ALLIN, playerUid);
        this.note = note;
    }
}
