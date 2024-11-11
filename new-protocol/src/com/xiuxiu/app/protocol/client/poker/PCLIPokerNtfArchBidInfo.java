package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfArchBidInfo {
    public int round; // 叫牌轮次，1-独庄，2-抄庄
    public int contract; // 参见 PCLIPokerReqArchBidInfo
    public long bidderUid; // 当前叫牌人UID，-1表示尚未有人叫牌
    public long nextBidderUid; // 下一个叫牌人UID

    @Override
    public String toString() {
        return "PCLIPokerNtfArchBidInfo{" +
                "round=" + round +
                ", contract=" + contract +
                ", bidderUid=" + bidderUid +
                ", nextBidderUid=" + nextBidderUid +
                '}';
    }
}
