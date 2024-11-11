package com.xiuxiu.app.protocol.client.club;

/**
 *
 */
public class PCLIClubReqClubGetApplyList {
    public long clubUid;
    public int applyType;    // 1 俱乐部申请列表 2 俱乐部合并申请列表

    @Override
    public String toString() {
        return "PCLIClubReqClubGetApplyList{" +
                "clubUid=" + clubUid +
                ", applyType=" + applyType +
                '}';
    }
}
