package com.xiuxiu.app.protocol.client.club;

public class PCLIClubReqApplyMergeOpt {
    public long fromClubUid; //发起申请的亲友圈
    public long toClubUid; //接收申请的亲友圈
    public int op;          // 0: 同意, 1: 拒绝

    @Override
    public String toString() {
        return "PCLIClubReqApplyMergeOpt{" +
                "fromClubUid=" + fromClubUid +
                ", toClubUid=" + toClubUid +
                ", op=" + op +
                '}';
    }
}
