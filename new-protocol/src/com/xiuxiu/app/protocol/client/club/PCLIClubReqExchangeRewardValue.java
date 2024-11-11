package com.xiuxiu.app.protocol.client.club;

public class PCLIClubReqExchangeRewardValue {
    public long  clubUid;
    public int value;//兑换得值

    @Override
    public String toString() {
        return "PCLIClubReqExchangeRewardValue{" +
                "clubUid='" + clubUid + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
