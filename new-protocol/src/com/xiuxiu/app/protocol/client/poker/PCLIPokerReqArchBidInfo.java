package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerReqArchBidInfo {
    public int contract; // 叫牌，0-不叫，1-叫牌

    @Override
    public String toString() {
        return "PCLIPokerReqArchBidInfo{" +
                "contract=" + contract +
                '}';
    }
}
