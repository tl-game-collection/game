package com.xiuxiu.app.protocol.client.club;

public class PCLIClubNtfAddMember {
    public long clubUid;          //clubUid
    public long playerUid;        //玩家uid
    public long uplinePlayerUid;  //上线玩家uid
    public int memberCnt;   // 俱乐部成员数量

    @Override
    public String toString() {
        return "PCLIClubNtfAddMember{" +
                "clubUid=" + clubUid +
                ", playerUid=" + playerUid +
                ", uplinePlayerUid=" + uplinePlayerUid +
                ", memberCnt=" + memberCnt +
                '}';
    }
}
