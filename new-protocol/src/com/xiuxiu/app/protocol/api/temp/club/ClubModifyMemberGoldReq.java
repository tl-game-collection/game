package com.xiuxiu.app.protocol.api.temp.club;

public class ClubModifyMemberGoldReq {
    public long clubUid;
    public long playerUid;
    public int gold;
    /**
     * type 0 后台普通修改，1 后台充值修改
     */
    public int type;
    public String sign;                     // md5(clubUid + playerUid + gold + type + key)

    @Override
    public String toString() {
        return "ClubModifyMemberGoldReq{" +
                "playerUid=" + playerUid +
                ", clubUid=" + clubUid +
                ", gold=" + gold +
                ", type=" + type +
                ", sign='" + sign + '\'' +
                '}';
    }
}
