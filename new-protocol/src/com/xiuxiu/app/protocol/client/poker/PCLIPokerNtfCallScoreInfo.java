package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfCallScoreInfo {
    public long callPlayerUid;
    public int score;
    public int maxScore;
    public long nextCallPlayerUid;  // 下一个叫分玩家uid

    @Override
    public String toString() {
        return "PCLIPokerNtfCallScoreInfo{" +
                "callPlayerUid=" + callPlayerUid +
                ", score=" + score +
                ", maxScore=" + maxScore +
                ", nextCallPlayerUid=" + nextCallPlayerUid +
                '}';
    }
}