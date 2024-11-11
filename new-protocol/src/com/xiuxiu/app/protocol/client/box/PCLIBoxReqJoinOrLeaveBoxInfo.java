package com.xiuxiu.app.protocol.client.box;

public class PCLIBoxReqJoinOrLeaveBoxInfo {
    public long clubUid;
    public long joinBoxUid;         // 加入包厢uid, 没有为-1
    public long leaveBoxUid;        // 离开包厢uid, 没有为-1

    @Override
    public String toString() {
        return "PCLIBoxReqJoinOrLeaveBoxInfo{" +
                "clubUid=" + clubUid +
                ", joinBoxUid=" + joinBoxUid +
                ", leaveBoxUid=" + leaveBoxUid +
                '}';
    }
}
