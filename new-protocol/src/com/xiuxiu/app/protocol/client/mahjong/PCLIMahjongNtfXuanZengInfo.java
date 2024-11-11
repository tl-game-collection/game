package com.xiuxiu.app.protocol.client.mahjong;


public class PCLIMahjongNtfXuanZengInfo {
    public long playerUid;
    public int value = -1;

    @Override
    public String toString() {
        return "PCLIMahjongNtfXuanZengInfo{" +
                "playerUid=" + playerUid +
                ", value=" + value +
                '}';
    }
}
