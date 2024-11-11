package com.xiuxiu.app.protocol.client.club;

public class PCLIClubReqAddMember {
    public long clubUid;
    public long playerUid;

    @Override
    public String toString() {
        return "PCLIClubReqAddMember{" +
                "clubUid=" + clubUid +
                ", playerUid=" + playerUid +
                '}';
    }
}
