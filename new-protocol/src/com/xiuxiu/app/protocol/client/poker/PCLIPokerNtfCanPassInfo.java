package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfCanPassInfo {
    public long passPlayerUid;

    public PCLIPokerNtfCanPassInfo() {

    }

    public PCLIPokerNtfCanPassInfo(long passPlayerUid) {
        this.passPlayerUid = passPlayerUid;
    }

    @Override
    public String toString() {
        return "PCLIPokerNtfCanPassInfo{" +
                "passPlayerUid=" + passPlayerUid +
                '}';
    }
}
