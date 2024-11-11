package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerReqHotPaiGowKeepHotInfo {
    public long playerUid;
    public int score;

    @Override
    public String toString() {
        return "PCLIPokerReqHotPaiGowKeepHotInfo{" +
                "playerUid=" + playerUid +
                ", score=" + score +
                '}';
    }
}
