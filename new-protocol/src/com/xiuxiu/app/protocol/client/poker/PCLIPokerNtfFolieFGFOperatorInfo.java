package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfFolieFGFOperatorInfo {
    public long operatorPlayerUid;          // 操作玩家uid

    public PCLIPokerNtfFolieFGFOperatorInfo(long operatorPlayerUid) {
        this.operatorPlayerUid = operatorPlayerUid;
    }

    @Override
    public String toString() {
        return "PCLIPokerNtfFolieFGFOperatorInfo{" +
                "operatorPlayerUid=" + operatorPlayerUid +
                '}';
    }
}
