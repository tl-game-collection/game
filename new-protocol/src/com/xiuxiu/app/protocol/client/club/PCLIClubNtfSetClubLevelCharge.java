package com.xiuxiu.app.protocol.client.club;

public class PCLIClubNtfSetClubLevelCharge {
    public long setClubUid;
    public long serviceCharge;//管理费

    @Override
    public String toString() {
        return "PCLIClubNtfSetClubLevelCharge{" +
                "setClubUid=" + setClubUid +
                ", serviceCharge=" + serviceCharge +
                '}';
    }
}
