package com.xiuxiu.app.protocol.client.poker;

import java.util.List;

public class PCLIPokerNtfReCallScoreInfo {
    public int bankerIndex;         // 先叫分
    public int myIndex;
    public List<Byte> myCards;
    public List<Byte> laiziCards;

    @Override
    public String toString() {
        return "PCLIPokerNtfReCallScoreInfo{" +
                "bankerIndex=" + bankerIndex +
                ", myIndex=" + myIndex +
                ", myCards=" + myCards +
                ", laiziCards=" + laiziCards +
                '}';
    }
}
