package com.xiuxiu.app.protocol.client.club.helper;

public class PCLIClubReqHelperInviteAnswer {
    public int status;//是否接受邀请(0拒绝1接受)
    public long clubUid; // 群uid
    public int roomId; //房间id

    @Override
    public String toString() {
        return "PCLIClubReqHelperInviteAnswer{" + "clubUid=" + clubUid + '}';
    }
}
