package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfArchScoreInfo {
    public long playerUid; // 获得积分的玩家UID
    public long source; // 积分来源，-1表示桌面积分，其他表示失去积分的玩家的UID
    public int score; // 分值

    @Override
    public String toString() {
        return "PCLIPokerNtfArchScoreInfo{" +
                "playerUid=" + playerUid +
                ", source=" + source +
                ", score=" + score +
                '}';
    }
}
