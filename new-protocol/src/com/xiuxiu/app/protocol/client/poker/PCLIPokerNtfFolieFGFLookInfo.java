package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfFolieFGFLookInfo {
    public long lookPlayerUid;          // 看牌玩家uid

    public PCLIPokerNtfFolieFGFLookInfo(long lookPlayerUid) {
        this.lookPlayerUid = lookPlayerUid;
    }

    @Override
    public String toString() {
        return "PCLIPokerNtfFGFLookInfo{" +
                "lookPlayerUid=" + lookPlayerUid +
                '}';
    }
}
