package com.xiuxiu.app.protocol.client.club;

public class PCLIClubReqPlayerInfo {
    public long playerUid;
    public long clubUid;

    @Override
    public String toString() {
        return "PCLIClubReqPlayerInfo{" +
                "playerUid=" + playerUid +
                ", clubUid=" + clubUid +
                '}';
    }
}
