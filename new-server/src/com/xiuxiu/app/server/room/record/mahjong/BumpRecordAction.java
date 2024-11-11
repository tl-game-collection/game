package com.xiuxiu.app.server.room.record.mahjong;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.normal.mahjong.BrightInfo;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.List;

public class BumpRecordAction extends RecordAction {
    protected long takePlayerUid;
    protected byte card;
    protected byte cardIndex;
    protected boolean bright;
    protected List<BrightInfo> brightInfo;

    public BumpRecordAction(long playerUid, long takePlayerUid, byte card, byte cardIndex, boolean bright, List<BrightInfo> brightInfo) {
        super(EActionOp.BUMP, playerUid);
        this.takePlayerUid = takePlayerUid;
        this.card = card;
        this.cardIndex = cardIndex;
        this.bright = bright;
        this.brightInfo = brightInfo;
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

    public byte getCardIndex() {
        return cardIndex;
    }

    public void setCardIndex(byte cardIndex) {
        this.cardIndex = cardIndex;
    }

    public boolean isBright() {
        return bright;
    }

    public void setBright(boolean bright) {
        this.bright = bright;
    }

    public List<BrightInfo> getBrightInfo() {
        return brightInfo;
    }

    public void setBrightInfo(List<BrightInfo> brightInfo) {
        this.brightInfo = brightInfo;
    }
}
