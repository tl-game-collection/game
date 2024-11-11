package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfTDKBetInfo {
    public int round;
    public int bet;
    public long playerUid;
    public Next next = new Next();

    @Override
    public String toString() {
        return "PCLIPokerNtfTDKBetInfo{" +
                "round=" + round +
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
