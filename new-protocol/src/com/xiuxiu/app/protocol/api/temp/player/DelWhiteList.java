package com.xiuxiu.app.protocol.api.temp.player;

public class DelWhiteList {
    public long playerUid;
    public String sign;

    @Override
    public String toString() {
        return "DelWhiteList{" +
                "playerUid=" + playerUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
