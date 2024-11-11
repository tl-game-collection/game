package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomNtfDissolveInfo {
    public int roomId;
    public long dissolvePlayerUid;              // 解散着uid

    @Override
    public String toString() {
        return "PCLIRoomNtfDissolveInfo{" +
                "roomId=" + roomId +
                ", dissolvePlayerUid=" + dissolvePlayerUid +
                '}';
    }
}
