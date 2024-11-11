package com.xiuxiu.app.protocol.client.club;

public class PCLIClubNtfDelMember {
    public long clubUid;
    public long playerUid;
    public int memberCnt;   // 俱乐部成员数量

    @Override
    public String toString() {
        return "PCLIClubNtfDelMember{" +
                "clubUid=" + clubUid +
                ", playerUid=" + playerUid +
                ", memberCnt=" + memberCnt +
                '}';
    }
}
