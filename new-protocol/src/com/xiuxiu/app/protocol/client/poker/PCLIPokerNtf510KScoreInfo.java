package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtf510KScoreInfo {
    // 玩家当前拿到的分数
    public long playerUid;
    public int wskScore;
    public int boomScore;


    @Override
    public String toString() {
        return "PCLIPokerNtf510KScoreInfo{" +
                "playerUid=" + playerUid +
                ", wskScore=" + wskScore +
                ", boomScore=" + boomScore +
                '}';
    }
}
