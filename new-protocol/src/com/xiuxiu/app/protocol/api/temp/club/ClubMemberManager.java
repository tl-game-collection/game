package com.xiuxiu.app.protocol.api.temp.club;

public class ClubMemberManager {
    public long playerUid;
    public long clubUid;
    public long targetPlayerUid;
    public String name;
    public String icon;
    public String sign;                     // md5(playerUid + clubUid + targetPlayerUid + name + icon + key)

    @Override
    public String toString() {
        return "ClubMemberManager{" +
                "playerUid=" + playerUid +
                ", clubUid=" + clubUid +
                ", targetPlayerUid=" + targetPlayerUid +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
