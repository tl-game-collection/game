package com.xiuxiu.app.protocol.client.club;

public class PCLIClubReqSetMemberJob {
    public long clubUid;
    public long playerUid;
    public int jobType;

    @Override
    public String toString() {
        return "PCLIClubReqSetMemberJob{" +
                "clubUid=" + clubUid +
                ", playerUid=" + playerUid +
                ", jobType=" + jobType +
                '}';
    }
}
