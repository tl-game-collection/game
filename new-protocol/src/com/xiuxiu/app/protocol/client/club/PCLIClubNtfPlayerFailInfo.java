package com.xiuxiu.app.protocol.client.club;

import com.xiuxiu.app.protocol.ErrorCode;

public class PCLIClubNtfPlayerFailInfo {
    public long clubUid;
    public long playerUid;
    public ErrorCode errorCode;

    @Override
    public String toString() {
        return "PCLIClubNtfPlayerFailInfo{" +
                "clubUid=" + clubUid +
                ", playerUid=" + playerUid +
                ", errorCode=" + errorCode +
                '}';
    }
}
