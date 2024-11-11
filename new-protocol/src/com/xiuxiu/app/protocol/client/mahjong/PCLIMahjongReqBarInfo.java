package com.xiuxiu.app.protocol.client.mahjong;

public class PCLIMahjongReqBarInfo {
    public byte cardValue;
    public byte startIndex;
    public byte endIndex;
    public byte insertIndex;

    @Override
    public String toString() {
        return "PCLIMahjongBarInfo{" +
                "cardValue=" + cardValue +
                ", startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                ", insertIndex=" + insertIndex +
                '}';
    }
}
