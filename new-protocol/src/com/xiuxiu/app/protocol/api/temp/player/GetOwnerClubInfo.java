package com.xiuxiu.app.protocol.api.temp.player;

public class GetOwnerClubInfo {
    public long playerUid;
    public String sign;                     // md5(playerUid + key)

    @Override
    public String toString() {
        return "GetOwnerClubInfo{" +
                "playerUid=" + playerUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
