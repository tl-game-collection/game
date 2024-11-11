package com.xiuxiu.app.server.room.record.mahjong;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong.EBarType;
import com.xiuxiu.app.server.room.record.RecordAction;

public class BarRecordAction extends RecordAction {
    protected long takePlayerUid;
    protected byte card;
    protected EBarType type;
    protected byte startIndex;
    protected byte endIndex;
    protected byte insertIndex;

    public BarRecordAction(long playerUid, long takePlayerUid, byte card, EBarType type, byte startIndex, byte endIndex, byte insertIndex) {
        super(EActionOp.BAR, playerUid);
        this.takePlayerUid = takePlayerUid;
        this.card = card;
        this.type = type;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.insertIndex = insertIndex;
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

    public EBarType getType() {
        return type;
    }

    public void setType(EBarType type) {
        this.type = type;
    }

    public byte getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(byte startIndex) {
        this.startIndex = startIndex;
    }

    public byte getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(byte endIndex) {
        this.endIndex = endIndex;
    }

    public byte getInsertIndex() {
        return insertIndex;
    }

    public void setInsertIndex(byte insertIndex) {
        this.insertIndex = insertIndex;
    }
}
