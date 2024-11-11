package com.xiuxiu.app.protocol.client.club;

public class PCLIClubReqApplyMerge {
    public long fromClubUid; //发起申请的亲友圈
    public long toClubUid; //接收申请的亲友圈

    @Override
    public String toString() {
        return "PCLIClubReqApplyMerge{" +
                "fromClubUid=" + fromClubUid +
                ", toClubUid=" + toClubUid +
                '}';
    }
}
