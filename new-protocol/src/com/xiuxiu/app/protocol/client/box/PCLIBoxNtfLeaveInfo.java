package com.xiuxiu.app.protocol.client.box;

public class PCLIBoxNtfLeaveInfo {
    public long groupUid;
    public long boxUid;
    public int roomIndex;
    public int allPlayerCnt;
    public long playerUid;

    @Override
    public String toString() {
        return "PCLIBoxNtfLeaveInfo{" +
                "groupUid=" + groupUid +
                ", boxUid=" + boxUid +
                ", roomIndex=" + roomIndex +
                ", allPlayerCnt=" + allPlayerCnt +
                ", playerUid=" + playerUid +
                '}';
    }
}
