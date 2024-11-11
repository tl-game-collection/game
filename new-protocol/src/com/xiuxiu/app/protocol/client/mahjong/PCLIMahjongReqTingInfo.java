package com.xiuxiu.app.protocol.client.mahjong;

public class PCLIMahjongReqTingInfo extends PCLIMahjongReqTakeInfo {
    public boolean ting;

    @Override
    public String toString() {
        return "PCLIMahjongReqTingInfo{" +
                "cardValue=" + cardValue +
                ", isLast=" + isLast +
                ", index=" + index +
                ", outputCardIndex=" + outputCardIndex +
                ", length=" + length +
                ", ting=" + ting +
                '}';
    }
}
