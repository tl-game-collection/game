package com.xiuxiu.app.protocol.api.temp.club;

public class ClubModifyUpLineReq {
    public long clubUid;
    public long playerUid;
    public long upLineUid;
    public String sign;                     // md5(clubUid + playerUid + upLineUid + key)

    @Override
    public String toString() {
        return "ClubMemberManager{" +
                "playerUid=" + playerUid +
                ", clubUid=" + clubUid +
                ", upLineUid=" + upLineUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
