package com.xiuxiu.app.protocol.client.club;

public class PCLIClubReqRoomDismiss {
    public int roomUid;
    public long clubUid;

    @Override
    public String toString() {
        return "PCLIClubReqRoomDismiss{" +
                "roomUid=" + roomUid +
                ", clubUid=" + clubUid +
                '}';
    }
}
