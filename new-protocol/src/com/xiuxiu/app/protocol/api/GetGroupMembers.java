package com.xiuxiu.app.protocol.api;

public class GetGroupMembers {
    public long playerUid;
    public long groupUid;
    public String sign;     // md5(playerUid + groupUid + key)

    @Override
    public String toString() {
        return "GetGroupMembers{" +
                "playerUid=" + playerUid +
                ", groupUid=" + groupUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
