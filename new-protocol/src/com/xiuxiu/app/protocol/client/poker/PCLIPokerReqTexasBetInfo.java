package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerReqTexasBetInfo {
    public int type; // 1-下注，2-跟注，3-加注，4-过，5-梭哈，6-放弃
    public int bet; // 筹码数量
    public boolean isBreakeven;//是否保本(用于竞技场保险选择)
    @Override
    public String toString() {
        return "PCLIPokerReqStudBetInfo{" +
                "type=" + type +
                ", bet=" + bet +
                ", isBreakeven=" + isBreakeven +
                '}';
    }
}
