package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfCanTakeInfo {
    public long takePlayerUid;

    public PCLIPokerNtfCanTakeInfo() {

    }

    public PCLIPokerNtfCanTakeInfo(long takePlayerUid) {
        this.takePlayerUid = takePlayerUid;
    }

    @Override
    public String toString() {
        return "PCLIPokerNtfCanTakeInfo{" +
                "takePlayerUid=" + takePlayerUid +
                '}';
    }
}
