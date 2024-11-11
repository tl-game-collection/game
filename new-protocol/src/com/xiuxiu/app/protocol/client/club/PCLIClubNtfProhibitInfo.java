package com.xiuxiu.app.protocol.client.club;

public class PCLIClubNtfProhibitInfo {
    public long clubUid;
    public long playerUid;
    public boolean isProhibit; //是否禁玩

    @Override
    public String toString() {
        return "PCLIClubNtfProhibitInfo{" +
                "clubUid=" + clubUid +
                ", playerUid=" + playerUid +
                ", isProhibit=" + isProhibit +
                '}';
    }
}
