package com.xiuxiu.app.protocol.api.temp.club;

public class GetDailyDiamondCost {
    public long timeBegin;
    public long timeEnd;
    public long clubUid = 0;
    public int gameType = 0;

    @Override
    public String toString() {
        return "GetDailyDiamondCost{" +
                "timeBegin=" + timeBegin +
                ", timeEnd=" + timeEnd +
                ", clubUid=" + clubUid +
                ", gameType=" + gameType +
                '}';
    }
}
