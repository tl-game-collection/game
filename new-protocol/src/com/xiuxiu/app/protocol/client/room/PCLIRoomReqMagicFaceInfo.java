package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomReqMagicFaceInfo {
    public int magicFaceId;
    public long toPlayerUid;
    public int type;        // 0: 房间, 1: 竞技场
    public long uid;        // 对应的uid

    @Override
    public String toString() {
        return "PCLIRoomReqMagicFaceInfo{" +
                "magicFaceId=" + magicFaceId +
                ", toPlayerUid=" + toPlayerUid +
                ", type=" + type +
                ", uid=" + uid +
                '}';
    }
}
