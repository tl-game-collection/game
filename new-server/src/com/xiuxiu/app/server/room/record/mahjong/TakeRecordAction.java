package com.xiuxiu.app.server.room.record.mahjong;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

public class TakeRecordAction extends RecordAction {
    protected byte card;
    protected byte last;
    protected byte cardIndex;
    protected byte outputCardIndex;
    protected int length;
    protected boolean auto;

    public TakeRecordAction(long playerUid, byte card, byte last, byte cardIndex, byte outputCardIndex, int length, boolean auto) {
        super(EActionOp.TAKE, playerUid);
        this.card = card;
        this.last = last;
        this.cardIndex = cardIndex;
        this.outputCardIndex = outputCardIndex;
        this.length = length;
        this.auto = auto;
    }

    public byte getCard() {
        return card;
    }

    public void setCard(byte card) {
        this.card = card;
    }

    public byte getLast() {
        return last;
    }

    public void setLast(byte last) {
        this.last = last;
    }

    public byte getCardIndex() {
        return cardIndex;
    }

    public void setCardIndex(byte cardIndex) {
        this.cardIndex = cardIndex;
    }

    public byte getOutputCardIndex() {
        return outputCardIndex;
    }

    public void setOutputCardIndex(byte outputCardIndex) {
        this.outputCardIndex = outputCardIndex;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }
}
