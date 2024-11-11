package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomReqKillInfo {
    public int roomId;
    public long killPlayerUid;

    @Override
    public String toString() {
        return "PCLIRoomReqKillInfo{" +
                "roomId=" + roomId +
                ", killPlayerUid=" + killPlayerUid +
                '}';
    }
}
