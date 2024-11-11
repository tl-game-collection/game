package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfTDKAnswerInfo {
    public int action; // 当前动作，2-跟注, 4-反踢
    public int bet; // 0-放弃，其他-跟注/反踢的数量
    public long playerUid;
    public Next next = new Next();

    @Override
    public String toString() {
        return "PCLIPokerNtfTDKAnswerInfo{" +
                "action=" + action +
                ", bet=" + bet +
                ", playerUid=" + playerUid +
                ", next=" + next +
                '}';
    }

    public static class Next {
        public int action;
        public long playerUid;

        @Override
        public String toString() {
            return "Next{" +
                    "action=" + action +
                    ", playerUid=" + playerUid +
                    '}';
        }
    }
}
