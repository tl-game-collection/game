package com.xiuxiu.app.protocol.api.temp.club;

public class GetClubRelations {
    public int clubUid;
    public String sign;     // md5(clubUid + key)

    @Override
    public String toString() {
        return "GetClubRelations{" +
                "clubUid=" + clubUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
