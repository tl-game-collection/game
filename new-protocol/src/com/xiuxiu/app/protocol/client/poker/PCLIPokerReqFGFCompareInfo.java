package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerReqFGFCompareInfo {
    public long comparePlayerUid;                  // 被比牌玩家uid

    @Override
    public String toString() {
        return "PCLIPokerReqFGFCompareInfo{" +
                "comparePlayerUid=" + comparePlayerUid +
                '}';
    }
}
