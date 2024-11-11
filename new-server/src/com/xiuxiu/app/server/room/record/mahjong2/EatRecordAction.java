package com.xiuxiu.app.server.room.record.mahjong2;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

public class EatRecordAction extends RecordAction {
    protected long takePlayerUid;
    protected byte card;
    protected int type;

    public EatRecordAction(long playerUid, long takePlayerUid, byte card, int type) {
        super(EActionOp.EAT, playerUid);
        this.takePlayerUid = takePlayerUid;
        this.card = card;
        this.type = type;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
