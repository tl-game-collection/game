package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfBlackJackRebetMulInfo {
    public int mul;         // 抢庄倍数, 0: 不抢 1:抢庄

    @Override
    public String toString() {
        return "PCLIPokerNtfBlackJackRebetMulInfo{" +
                "mul=" + mul +
                '}';
    }
}
