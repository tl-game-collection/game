package com.xiuxiu.app.server.room.record.poker;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

public class CompareCardRecordAction extends RecordAction {
    private long initiatorPlayerUid;            // 发起方
    private long receiverPlayerUid;             // 接收方
    private long winPlayerUid;                  // 赢
    private long lostPlayerUid;                 // 输
    private long note;                          // 筹码


    public CompareCardRecordAction(long initiatorPlayerUid, long receiverPlayerUid, long winPlayerUid, long lostPlayerUid, int note) {
        super(EActionOp.COMPARE, -1);
        this.initiatorPlayerUid = initiatorPlayerUid;
        this.receiverPlayerUid = receiverPlayerUid;
        this.winPlayerUid = winPlayerUid;
        this.lostPlayerUid = lostPlayerUid;
        this.note = note;
    }

    public long getNote() {
        return note;
    }

    public void setNote(long note) {
        this.note = note;
    }

    public long getInitiatorPlayerUid() {
        return initiatorPlayerUid;
    }

    public void setInitiatorPlayerUid(long initiatorPlayerUid) {
        this.initiatorPlayerUid = initiatorPlayerUid;
    }

    public long getReceiverPlayerUid() {
        return receiverPlayerUid;
    }

    public void setReceiverPlayerUid(long receiverPlayerUid) {
        this.receiverPlayerUid = receiverPlayerUid;
    }

    public long getWinPlayerUid() {
        return winPlayerUid;
    }

    public void setWinPlayerUid(long winPlayerUid) {
        this.winPlayerUid = winPlayerUid;
    }

    public long getLostPlayerUid() {
        return lostPlayerUid;
    }

    public void setLostPlayerUid(long lostPlayerUid) {
        this.lostPlayerUid = lostPlayerUid;
    }
}
