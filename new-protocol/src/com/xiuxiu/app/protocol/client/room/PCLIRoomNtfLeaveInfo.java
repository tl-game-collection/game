package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomNtfLeaveInfo {
    public int roomId;
    public long playerUid;

    @Override
    public String toString() {
        return "PCLIRoomNtfLeaveInfo{" +
                "roomId=" + roomId +
                ", playerUid=" + playerUid +
                '}';
    }
}
