package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfRobRedEnvelopeResult {
    public long playerUid;                      // 抢完红包玩家
    public String playerName;                   // 玩家姓名
    public String playerIcon;                   // 玩家头像
    public String sum;                          // 红包金额

    @Override
    public String toString() {
        return "PCLIPokerNtfRobRedEnvelopeResult{" +
                "playerUid=" + playerUid +
                ", playerName='" + playerName + '\'' +
                ", playerIcon='" + playerIcon + '\'' +
                ", sum='" + sum + '\'' +
                '}';
    }
}
