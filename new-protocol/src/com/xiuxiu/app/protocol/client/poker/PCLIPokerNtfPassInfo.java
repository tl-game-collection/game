package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfPassInfo {
    public long passPlayerUid;
    public long nextTakePlayerUid;

    public PCLIPokerNtfPassInfo() {

    }

    public PCLIPokerNtfPassInfo(long passPlayerUid, long nextTakePlayerUid) {
        this.passPlayerUid = passPlayerUid;
        this.nextTakePlayerUid = nextTakePlayerUid;
    }

    @Override
    public String toString() {
        return "PCLIPokerNtfPassInfo{" +
                "passPlayerUid=" + passPlayerUid +
                ", nextTakePlayerUid=" + nextTakePlayerUid +
                '}';
    }
}
