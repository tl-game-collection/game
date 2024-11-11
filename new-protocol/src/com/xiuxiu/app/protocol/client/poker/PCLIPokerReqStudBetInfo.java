package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerReqStudBetInfo {
    public int type; // 1-下注，2-跟注，3-加注，4-过，5-梭哈，6-放弃
    public int bet; // 筹码数量

    @Override
    public String toString() {
        return "PCLIPokerReqStudBetInfo{" +
                "type=" + type +
                ", bet=" + bet +
                '}';
    }
}
