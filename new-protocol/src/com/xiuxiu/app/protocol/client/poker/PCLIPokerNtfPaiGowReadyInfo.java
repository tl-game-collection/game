package com.xiuxiu.app.protocol.client.poker;


public class PCLIPokerNtfPaiGowReadyInfo {
    public long playerUid;
    public boolean ready;

    @Override
    public String toString() {
        return "PCLIPokerNtfPaiGowReadyInfo{" +
                "playerUid=" + playerUid +
                ", ready=" + ready +
                '}';
    }
}
