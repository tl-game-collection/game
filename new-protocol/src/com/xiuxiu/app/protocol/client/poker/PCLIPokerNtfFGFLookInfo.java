package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfFGFLookInfo {
    public long lookPlayerUid;          // 看牌玩家uid

    public PCLIPokerNtfFGFLookInfo(long lookPlayerUid) {
        this.lookPlayerUid = lookPlayerUid;
    }

    @Override
    public String toString() {
        return "PCLIPokerNtfFGFLookInfo{" +
                "lookPlayerUid=" + lookPlayerUid +
                '}';
    }
}
