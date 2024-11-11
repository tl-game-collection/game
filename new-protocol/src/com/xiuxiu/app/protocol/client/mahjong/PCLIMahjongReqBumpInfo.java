package com.xiuxiu.app.protocol.client.mahjong;

public class PCLIMahjongReqBumpInfo {
    public byte cardValue;
    public byte index;

    @Override
    public String toString() {
        return "PCLIMahjongReqBumpInfo{" +
                "cardValue=" + cardValue +
                ", index=" + index +
                '}';
    }
}
