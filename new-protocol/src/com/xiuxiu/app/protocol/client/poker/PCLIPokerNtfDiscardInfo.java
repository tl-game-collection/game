package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfDiscardInfo {
    public long playerUid;                  // 弃牌玩家uid

    @Override
    public String toString() {
        return "PCLIPokerNtfDiscardInfo{" +
                "playerUid=" + playerUid +
                '}';
    }
}
