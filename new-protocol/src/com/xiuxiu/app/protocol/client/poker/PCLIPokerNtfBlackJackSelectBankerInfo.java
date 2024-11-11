package com.xiuxiu.app.protocol.client.poker;

/**
 *
 */
public class PCLIPokerNtfBlackJackSelectBankerInfo {
    public long selectPlayerUid;        // 庄家玩家uid

    @Override
    public String toString() {
        return "PCLIPokerNtfBlackJackSelectBankerInfo{" +
                "selectPlayerUid=" + selectPlayerUid +
                '}';
    }
}
