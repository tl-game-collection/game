package com.xiuxiu.app.protocol.client.club;

public class PCLIClubNtfSetMemberJob {
    public long clubUid;
    public long playerUid;
    public int jobType;

    @Override
    public String toString() {
        return "PCLIClubNtfSetMemberJob{" +
                "clubUid=" + clubUid +
                ", playerUid=" + playerUid +
                ", jobType=" + jobType +
                '}';
    }
}
