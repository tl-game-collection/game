package com.xiuxiu.app.protocol.api.temp.club;

public class GetClubMemberList {
    public long playerUid;
    public long clubUid;
    public long searchUid;
    public int page;
    public int pageSize;
    public String sign;                     // md5(playerUid + clubUid + searchUid + page + pageSize + key)

    @Override
    public String toString() {
        return "GetClubMemberList{" +
                "playerUid=" + playerUid +
                ", clubUid=" + clubUid +
                ", searchUid=" + searchUid +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", sign='" + sign + '\'' +
                '}';
    }
}
