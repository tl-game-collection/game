package com.xiuxiu.app.protocol.api;

public class GetDailyActiveCount {
    public long timeBegin;
    public long timeEnd;

    @Override
    public String toString() {
        return "GetDailyActiveCount{" +
                "timeBegin=" + timeBegin +
                ", timeEnd=" + timeEnd +
                '}';
    }
}
