package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfPaiGowAllRebetInfo {
    public int base;
    public int oneReb;
    public int twoReb;
    public int threeReb;
    public long remain;

    @Override
    public String toString() {
        return "PCLIPokerNtfPaiGowAllRebetInfo{" +
                "base=" + base +
                ", oneReb=" + oneReb +
                ", twoReb=" + twoReb +
                ", threeReb=" + threeReb +
                ", remain=" + remain +
                '}';
    }
}
