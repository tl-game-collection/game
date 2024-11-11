package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfSetTakeInfo {
    public long nextTakePlayerUid;

    public PCLIPokerNtfSetTakeInfo() {

    }

    public PCLIPokerNtfSetTakeInfo(long nextTakePlayerUid) {
        this.nextTakePlayerUid = nextTakePlayerUid;
    }

    @Override
    public String toString() {
        return "PCLIPokerNtfPassInfo{" +
                " nextTakePlayerUid=" + nextTakePlayerUid +
                '}';
    }
}
