package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfHotPaiGowKeepHotInfo {
    public long playerUid;
    public int onScore;
    public String leftScore;

    @Override
    public String toString() {
        return "PCLIPokerNtfHotPaiGowKeepHotInfo{" +
                "playerUid=" + playerUid +
                ", onScore=" + onScore +
                ", leftScore='" + leftScore + '\'' +
                '}';
    }
}
