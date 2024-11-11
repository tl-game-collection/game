package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomNtfDissolveAgreeInfo {
    public int roomId;
    public long opPlayerUid;

    @Override
    public String toString() {
        return "PCLIRoomNtfDissolveAgreeInfo{" +
                "roomId=" + roomId +
                ", opPlayerUid=" + opPlayerUid +
                '}';
    }
}
