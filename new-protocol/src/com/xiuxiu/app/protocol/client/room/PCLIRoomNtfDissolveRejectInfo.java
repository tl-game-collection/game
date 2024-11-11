package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomNtfDissolveRejectInfo {
    public int roomId;
    public long rejectPlayerUid;

    @Override
    public String toString() {
        return "PCLIRoomNtfDissolveRejectInfo{" +
                "roomId=" + roomId +
                ", rejectPlayerUid=" + rejectPlayerUid +
                '}';
    }
}
