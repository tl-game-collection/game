package com.xiuxiu.app.server.room.record.mahjong2;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

public class BarRecordAction extends RecordAction {
    protected long takePlayerUid;
    protected byte card;

    public BarRecordAction(long playerUid, long takePlayerUid, byte card) {
        super(EActionOp.BAR, playerUid);
        this.takePlayerUid = takePlayerUid;
        this.card = card;
    }

    public long getTakePlayerUid() {
        return takePlayerUid;
    }

    public void setTakePlayerUid(long takePlayerUid) {
        this.takePlayerUid = takePlayerUid;
    }

    public byte getCard() {
        return card;
    }

    public void setCard(byte card) {
        this.card = card;
    }
}
