package com.xiuxiu.app.server.room.record.mahjong;

import com.xiuxiu.app.server.room.record.RecordRoomBriefInfo;

public class RecordMahjongRoomBriefInfo extends RecordRoomBriefInfo {
    protected int crap1;
    protected int crap2;
    protected byte chaoCard;
    protected byte laiZi;

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

    public byte getChaoCard() {
        return chaoCard;
    }

    public void setChaoCard(byte chaoCard) {
        this.chaoCard = chaoCard;
    }

    public byte getLaiZi() {
        return laiZi;
    }

    public void setLaiZi(byte laiZi) {
        this.laiZi = laiZi;
    }
}
