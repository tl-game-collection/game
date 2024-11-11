package com.xiuxiu.app.protocol.api.temp.club;

public class AddClubMember {
    public long clubUid;
    public long playerUid;
    public String sign;// md5(playerUid +clubUid + key)

    @Override
    public String toString() {
        return "AddClubMember{" +
                "clubUid=" + clubUid +
                ", playerUid=" + playerUid +
                '}';
    }
}
