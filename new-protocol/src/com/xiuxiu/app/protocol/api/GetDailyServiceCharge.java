package com.xiuxiu.app.protocol.api;

public class GetDailyServiceCharge {
    public long timeBegin;
    public long timeEnd;
    public long groupUid;

    @Override
    public String toString() {
        return "GetDailyServiceCharge{" +
                "timeBegin=" + timeBegin +
                ", timeEnd=" + timeEnd +
                ", groupUid=" + groupUid +
                '}';
    }
}
