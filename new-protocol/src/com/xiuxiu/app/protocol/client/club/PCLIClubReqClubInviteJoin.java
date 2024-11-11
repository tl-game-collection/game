package com.xiuxiu.app.protocol.client.club;

/**
 *
 */
public class PCLIClubReqClubInviteJoin {
    public long clubUid;           // 要加入的群Uid
    public long invitorUid;        // 邀请人Uid

    @Override
    public String toString() {
        return "PCLIClubReqClubInviteJoin{" +
                "clubUid=" + clubUid +
                ", invitorUid=" + invitorUid +
                '}';
    }
}
