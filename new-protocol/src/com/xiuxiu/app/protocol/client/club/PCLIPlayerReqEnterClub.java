package com.xiuxiu.app.protocol.client.club;

public class PCLIPlayerReqEnterClub {

    /**
     * 俱乐部id
     */
    public long clubUid;
    /**
     * 选择的亲友圈uid
     */
    public long newClubUid;

    @Override
    public String toString() {
        return "PCLIPlayerReqEnterClub{" +
                "clubUid=" + clubUid +
                ", newClubUid=" + newClubUid +
                '}';
    }
}
