package com.xiuxiu.app.protocol.client.mahjong;

public class PCLIMahjongNtfChangePaoCntInfo {
    public byte card;
    public int newCnt;

    @Override
    public String toString() {
        return "PCLIMahjongNtfChangePaoCntInfo{" +
                "card=" + card +
                ", newCnt=" + newCnt +
                '}';
    }
}
