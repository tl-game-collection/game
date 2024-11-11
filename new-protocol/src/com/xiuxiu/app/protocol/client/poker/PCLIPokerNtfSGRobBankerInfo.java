package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfSGRobBankerInfo {
    public long playerUid;
    public int mul;         // 抢庄倍数, 0: 不抢庄

    @Override
    public String toString() {
        return "PCLIPokerNtfSGRobBankerInfo{" +
                "playerUid=" + playerUid +
                ", mul=" + mul +
                '}';
    }
}
