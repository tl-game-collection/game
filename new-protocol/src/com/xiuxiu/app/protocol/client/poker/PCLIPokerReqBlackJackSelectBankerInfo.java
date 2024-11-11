package com.xiuxiu.app.protocol.client.poker;

/**
 *
 */
public class PCLIPokerReqBlackJackSelectBankerInfo {
    public int selectState;           // 0 放弃，1 选择要庄

    @Override
    public String toString() {
        return "PCLIPokerReqBlackJackSelectBankerInfo{" +
                "selectState=" + selectState +
                '}';
    }
}
