package com.xiuxiu.app.protocol.client.mahjong;


public class PCLIMahjongNtfDingQueInfo {
    public long playerUid;
    public int color = -1;

    @Override
    public String toString() {
        return "PCLIMahjongNtfDingQueInfo{" +
                "playerUid=" + playerUid +
                ", color=" + color +
                '}';
    }
}
