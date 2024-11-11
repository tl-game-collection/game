package com.xiuxiu.app.protocol.api;

public class DailyShareAward {
    public long groupUid;
    public long playerUid;
    public String sign;

    @Override
    public String toString() {
        return "DailyShareAward{" +
                "groupUid=" + groupUid +
                ", playerUid=" + playerUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
