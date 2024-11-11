package com.xiuxiu.app.protocol.client.mahjong;


public class PCLIMahjongNtfStartHuInfo {
    public long playerUid;
    public boolean selected;

    @Override
    public String toString() {
        return "PCLIMahjongNtfStartHuInfo{" +
                "playerUid=" + playerUid +
                ", selected=" + selected +
                '}';
    }
}
