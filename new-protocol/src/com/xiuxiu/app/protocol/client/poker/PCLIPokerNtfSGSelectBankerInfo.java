package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfSGSelectBankerInfo {

    public long selectPlayerUid;        // 庄家玩家uid

    @Override
    public String toString() {
        return "PCLIPokerNtfSGSelectBankerInfo{" +
                "selectPlayerUid=" + selectPlayerUid +
                '}';
    }
}
