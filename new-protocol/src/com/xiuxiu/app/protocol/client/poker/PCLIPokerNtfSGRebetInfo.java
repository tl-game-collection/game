package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfSGRebetInfo {
    public long playerUid;
    public int rebet;       // 下注

    @Override
    public String toString() {
        return "PCLIPokerNtfSGRebetInfo{" +
                "playerUid=" + playerUid +
                ", rebet=" + rebet +
                '}';
    }
}
