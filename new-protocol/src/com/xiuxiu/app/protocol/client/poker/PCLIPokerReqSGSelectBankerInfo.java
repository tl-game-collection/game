package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerReqSGSelectBankerInfo {
    public int selectState;           // 0 放弃，1 选择要庄

    @Override
    public String toString() {
        return "PCLIPokerReqSGSelectBankerInfo{" +
                "selectState=" + selectState +
                '}';
    }
}
