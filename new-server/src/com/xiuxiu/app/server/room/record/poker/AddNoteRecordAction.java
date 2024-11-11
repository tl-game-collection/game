package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

public class AddNoteRecordAction extends RecordAction {
    private int note;
    private int isFillUp;

    public int getIsFillUp() {
        return isFillUp;
    }

    public void setIsFillUp(int isFillUp) {
        this.isFillUp = isFillUp;
    }


    public AddNoteRecordAction(long playerUid, int note, int isFillUp) {
        super(EActionOp.ADD_NOTE, playerUid);
        this.note = note;
        this.isFillUp = isFillUp;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }
}
