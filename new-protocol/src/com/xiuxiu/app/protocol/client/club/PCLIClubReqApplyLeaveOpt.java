package com.xiuxiu.app.protocol.client.club;

public class PCLIClubReqApplyLeaveOpt {
    public long leaveClubUid; //发起申请的亲友圈
    public int op;          // 0: 同意, 1: 拒绝

    @Override
    public String toString() {
        return "PCLIClubReqApplyLeaveOpt{" +
                "leaveClubUid=" + leaveClubUid +
                ", op=" + op +
                '}';
    }
}