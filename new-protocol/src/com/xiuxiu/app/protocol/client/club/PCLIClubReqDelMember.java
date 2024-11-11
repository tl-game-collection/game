package com.xiuxiu.app.protocol.client.club;

public class PCLIClubReqDelMember {
    public long clubUid;
    public long playerUid;

    @Override
    public String toString() {
        return "PCLIClubReqDelMember{" +
                "clubUid=" + clubUid +
                ", playerUid=" + playerUid +
                '}';
    }
}
