package com.xiuxiu.app.protocol.client.club.helper;

public class PCLIClubReqHelperInvitePlayers {
    public long clubUid; // 群uid
    public int page;// 页码
    public int size;// 每页多少

    @Override
    public String toString() {
        return "PCLIClubHelperInvitePlayers{" + "clubUid=" + clubUid + '}';
    }
}
