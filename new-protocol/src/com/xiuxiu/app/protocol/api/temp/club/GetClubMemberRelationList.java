package com.xiuxiu.app.protocol.api.temp.club;

public class GetClubMemberRelationList {
    public long clubUid;    // 俱乐部UID
    public String sign;     // md5(clubUid + key)

    @Override
    public String toString() {
        return "GetClubMemberRelationList{" +
                "clubUid=" + clubUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}