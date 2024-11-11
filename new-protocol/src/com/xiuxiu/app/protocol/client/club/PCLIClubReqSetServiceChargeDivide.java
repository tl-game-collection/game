package com.xiuxiu.app.protocol.client.club;

public class PCLIClubReqSetServiceChargeDivide {
    public long clubUid;           // 群uid
    public int divide;              // 分成比例，针对成员

    @Override
    public String toString() {
        return "PCLIClubReqSetServiceChargeDivide{" +
                "clubUid=" + clubUid +
                ", divide=" + divide +
                '}';
    }
}
