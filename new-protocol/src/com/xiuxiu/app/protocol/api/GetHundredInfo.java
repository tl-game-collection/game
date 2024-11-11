package com.xiuxiu.app.protocol.api;

public class GetHundredInfo {
    public long groupUid;
    public long arenaUid;
    public String token;

    @Override
    public String toString() {
        return "GetHundredInfo{" +
                "groupUid=" + groupUid +
                ", arenaUid=" + arenaUid +
                ", token='" + token + '\'' +
                '}';
    }
}
