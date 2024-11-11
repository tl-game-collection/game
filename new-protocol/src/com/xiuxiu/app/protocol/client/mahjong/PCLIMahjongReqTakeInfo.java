package com.xiuxiu.app.protocol.client.mahjong;

public class PCLIMahjongReqTakeInfo {
    public byte cardValue;
    public byte isLast;//0，1两种值
    public byte index;
    public byte outputCardIndex;
    public int length;
    public byte desktingIndex;//牌桌听牌的位置
    

    @Override
    public String toString() {
        return "PCLIMahjongReqTakeInfo{" +
                "cardValue=" + cardValue +
                ", isLast=" + isLast +
                ", index=" + index +
                ", outputCardIndex=" + outputCardIndex +
                ", length=" + length +
                "desktingIndex"+desktingIndex+
                '}';
    }
}
