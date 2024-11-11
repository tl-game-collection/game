package com.xiuxiu.app.protocol.client.poker;

import java.util.List;

public class PCLIPokerNtfTDKNewRoundInfo {
    public int round; // 第几轮，从1开始
    public long playerUid; // 起始玩家UID
    public List<RoundPlayer> players; // 本轮参与的玩家信息

    public static class RoundPlayer {
        public long uid;
        public byte card;
        public boolean sharedCard; // 借牌

        @Override
        public String toString() {
            return "RoundPlayer{" +
                    "uid=" + uid +
                    ", card=" + card +
                    ", sharedCard=" + sharedCard +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PCLIPokerNtfTDKNewRoundInfo{" +
                "round=" + round +
                ", playerUid=" + playerUid +
                ", players=" + players +
                '}';
    }
}
