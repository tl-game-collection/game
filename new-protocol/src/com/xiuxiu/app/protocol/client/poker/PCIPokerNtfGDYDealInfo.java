package com.xiuxiu.app.protocol.client.poker;

public class PCIPokerNtfGDYDealInfo {
    public long playerUid;
    public Byte card;
    public int leftCardSize;

    @Override
    public String toString() {
        return "PCIPokerNtfGDYDealInfo{" +
                "playerUid=" + playerUid +
                ", card=" + card +
                ", cardSize=" + leftCardSize +
                '}';
    }
}
