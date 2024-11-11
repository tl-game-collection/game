package com.xiuxiu.app.server.room.record.mahjong2;

import com.xiuxiu.app.server.room.record.RecordRoomBriefInfo;

import java.util.ArrayList;
import java.util.List;

public class RecordMahjongRoomBriefInfo extends RecordRoomBriefInfo {
    protected int crap1;
    protected int crap2;
    protected byte laiZiCard = -1;
    protected List<Byte> piList = new ArrayList<>();

    public int getCrap1() {
        return crap1;
    }

    public void setCrap1(int crap1) {
        this.crap1 = crap1;
    }

    public int getCrap2() {
        return crap2;
    }

    public void setCrap2(int crap2) {
        this.crap2 = crap2;
    }

    public byte getLaiZiCard() {
        return laiZiCard;
    }

    public void setLaiZiCard(byte laiZiCard) {
        this.laiZiCard = laiZiCard;
    }

    public List<Byte> getPiList() {
        return piList;
    }

    public void setPiList(List<Byte> piList) {
        this.piList = piList;
    }
}
