package com.xiuxiu.app.protocol.api.temp.club;

public class GetClubMemberDataReq {
    public long clubUid;
    public long playerUid;
    public long searchUid;
    public int page;
    public int pageSize;
    public String sign;                     // md5(clubUid + playerUid + key)

    @Override
    public String toString() {
        return "GetLineDataReq{" +
                "clubUid=" + clubUid +
                ", playerUid=" + playerUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
