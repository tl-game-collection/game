package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfFGFOperatorInfo {
    public long operatorPlayerUid;          // 操作玩家uid
    public long timeout;

    public PCLIPokerNtfFGFOperatorInfo(long operatorPlayerUid,long timeout) {
        this.operatorPlayerUid = operatorPlayerUid;
        this.timeout=timeout;
    }

    @Override
    public String toString() {
        return "PCLIPokerNtfFGFOperatorInfo{" +
                "operatorPlayerUid=" + operatorPlayerUid +
                "timeout=" + timeout +
                '}';
    }
}
