package com.xiuxiu.app.protocol.client.club;

public class PCLIClubReqSetClubLevelCharge {
    public long clubUid;
    public long setClubUid;   //被设置的下级圈
    public int serviceCharge;//管理费

    @Override
    public String toString() {
        return "PCLIClubReqSetClubLevelCharge{" +
                "clubUid=" + clubUid +
                ", setClubUid=" + setClubUid +
                ", serviceCharge=" + serviceCharge +
                '}';
    }
}
