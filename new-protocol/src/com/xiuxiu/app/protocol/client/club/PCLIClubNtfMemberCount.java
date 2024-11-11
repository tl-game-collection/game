package com.xiuxiu.app.protocol.client.club;

/**
 *
 */
public class PCLIClubNtfMemberCount {
    public long clubUid ;   // 俱乐部Uid
    public int memberCnt;   // 俱乐部成员数量

    @Override
    public String toString() {
        return "PCLIClubNtfMemberCount{" +
                "clubUid=" + clubUid +
                ", memberCnt=" + memberCnt +
                '}';
    }
}
