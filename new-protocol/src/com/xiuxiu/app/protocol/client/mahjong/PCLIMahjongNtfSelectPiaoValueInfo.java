package com.xiuxiu.app.protocol.client.mahjong;

public class PCLIMahjongNtfSelectPiaoValueInfo {
    public long playerUid;
    public int value;

    @Override
    public String toString() {
        return "PCLIMahjongNtfSelectPiaoValueInfo{" +
                "playerUid=" + playerUid +
                ", value=" + value +
                '}';
    }
}
