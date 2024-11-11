package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

public class FollowNoteRecordAction extends RecordAction {
    private int note;

    public FollowNoteRecordAction(long playerUid, int note) {
        super(EActionOp.FOLLOW_NOTE, playerUid);
        this.note = note;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }
}
