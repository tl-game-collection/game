package com.xiuxiu.app.server.room.normal.mahjong2;

public class HuInfo {
	//自摸
    protected boolean ziMo;
    protected long takePlayerUid;
    //牌型
    protected EPaiXing paiXing = EPaiXing.NONE;
    protected int fang;
    //胡牌
    protected byte huCard;

    public HuInfo() {
    }

    public HuInfo(long takePlayerUid, EPaiXing paiXing, byte huCard, int fang) {
        this.ziMo = -1 == takePlayerUid;
        this.takePlayerUid = takePlayerUid;
        this.paiXing = paiXing;
        this.huCard = huCard;
        this.fang = fang;
    }

    public boolean isZiMo() {
        return ziMo;
    }

    public void setZiMo(boolean ziMo) {
        this.ziMo = ziMo;
    }

    public long getTakePlayerUid() {
        return takePlayerUid;
    }

    public void setTakePlayerUid(long takePlayerUid) {
        this.takePlayerUid = takePlayerUid;
    }

    public EPaiXing getPaiXing() {
        return paiXing;
    }

    public void setPaiXing(EPaiXing paiXing) {
        this.paiXing = paiXing;
    }

    public byte getHuCard() {
        return huCard;
    }

    public void setHuCard(byte huCard) {
        this.huCard = huCard;
    }

    public int getFang() {
        return fang;
    }

    public void setFang(int fang) {
        this.fang = fang;
    }
}
