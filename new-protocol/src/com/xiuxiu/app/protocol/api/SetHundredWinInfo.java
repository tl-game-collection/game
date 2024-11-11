package com.xiuxiu.app.protocol.api;

public class SetHundredWinInfo {
    public long clubUid;
    public int roomId;
    public int win;
    public String token;

    @Override
    public String toString() {
        return "SetHundredWinInfo{" +
                "clubUid=" + clubUid +
                ", roomId=" + roomId +
                ", win=" + win +
                ", token='" + token + '\'' +
                '}';
    }
}
