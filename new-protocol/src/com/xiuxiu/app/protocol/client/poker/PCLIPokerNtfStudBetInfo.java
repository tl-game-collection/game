package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfStudBetInfo {
    public long playerUid; // 下注的玩家UID
    public int type; // 下注的类型，1-下注，2-跟注，3-加注，4-过，5-梭哈，6-放弃
    public int bet; // 下注的筹码数量

    public long bossUid; // 本轮说话人的UID
    public int bossBetType; // 本轮说话人的下注类型，1-下注，3-加注，4-过，6-放弃，优先级：加注 > 下注 > 过 = 放弃
    public int bossBet; // 本轮说话人在本轮的总下注值

    public Next next = new Next(); // 下一个动作

    public static class Next {
        public long playerUid; // 玩家UID
        public int action; // 0 - 无动作

        @Override
        public String toString() {
            return "Next{" +
                    "playerUid=" + playerUid +
                    ", action=" + action +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PCLIPokerNtfStudBetInfo{" +
                "playerUid=" + playerUid +
                ", type=" + type +
                ", bet=" + bet +
                ", bossUid=" + bossUid +
                ", bossBetType=" + bossBetType +
                ", bossBet=" + bossBet +
                ", next=" + next +
                '}';
    }
}
