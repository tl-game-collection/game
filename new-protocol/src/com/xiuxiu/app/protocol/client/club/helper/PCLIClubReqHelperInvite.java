package com.xiuxiu.app.protocol.client.club.helper;

public class PCLIClubReqHelperInvite {
    public long clubUid; // 群uid
    public int roomId;// 房间id
    public long inviteUid;// 被邀请人uid

    @Override
    public String toString() {
        return "PCLIClubHelperInvitePlayers{" + "clubUid=" + clubUid + '}';
    }
}
