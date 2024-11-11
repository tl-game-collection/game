package com.xiuxiu.app.protocol.client.club;

/**
 *
 */
public class PCLIClubReqClubOpApplyList {
    public long clubUid;
    public long playerUid;
    public int op;          // 0: 同意, 1: 拒绝

    @Override
    public String toString() {
        return "PCLIClubReqClubOpApplyList{" +
                "clubUid=" + clubUid +
                ", playerUid=" + playerUid +
                ", op=" + op +
                '}';
    }
}
