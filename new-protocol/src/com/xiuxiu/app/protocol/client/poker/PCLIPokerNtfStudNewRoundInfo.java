package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfStudNewRoundInfo {
    public int round; // 轮次，从1开始
    public long playerUidToBet; // 说话人的玩家UID
    public List<PlayerData> players = new ArrayList<>(); // 本轮玩家信息

    public static class PlayerData {
        public long playerUid; // 玩家UID
        public byte card; // 本轮获得的牌

        @Override
        public String toString() {
            return "PlayerData{" +
                    "playerUid=" + playerUid +
                    ", card=" + card +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PCLIPokerNtfStudNewRoundInfo{" +
                "round=" + round +
                ", playerUidToBet=" + playerUidToBet +
                ", players=" + players +
                '}';
    }
}
