package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomNtfMagicFaceInfo {
    public int magicFaceId;
    public long fromPlayerUid;
    public long toPlayerUid;

    @Override
    public String toString() {
        return "PCLIRoomNtfMagicFaceInfo{" +
                "magicFaceId=" + magicFaceId +
                ", fromPlayerUid=" + fromPlayerUid +
                ", toPlayerUid=" + toPlayerUid +
                '}';
    }
}
