package com.xiuxiu.app.server.room.record.mahjong2;

import com.xiuxiu.app.server.room.normal.action.EActionOp;
import com.xiuxiu.app.server.room.record.RecordAction;

import java.util.ArrayList;
import java.util.List;

public class OpenBarRecordAction extends RecordAction {
    private boolean select;
    private int cap1;
    private int cap2;
    private List<Byte> card = new ArrayList<>();

    public OpenBarRecordAction(long playerUid) {
        super(EActionOp.OPEN_BAR, playerUid);
    }

    public boolean isSelect() {
        return select;
    }

    public void addCard(List<Byte> card) {
        this.card.addAll(card);
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public int getCap1() {
        return cap1;
    }

    public void setCap1(int cap1) {
        this.cap1 = cap1;
    }

    public int getCap2() {
        return cap2;
    }

    public void setCap2(int cap2) {
        this.cap2 = cap2;
    }

    public List<Byte> getCard() {
        return card;
    }

    public void setCard(List<Byte> card) {
        this.card = card;
    }
}
