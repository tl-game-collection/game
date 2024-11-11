package com.xiuxiu.app.protocol.client.mahjong;


public class PCLIMahjongNtfXuanPiaoInfo {
    public long playerUid;
    public int value = -1;

    @Override
    public String toString() {
        return "PCLIMahjongNtfXuanPiaoInfo{" +
                "playerUid=" + playerUid +
                ", value=" + value +
                '}';
    }
}
