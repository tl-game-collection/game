package com.xiuxiu.app.protocol.client.floor;

public class PCLIFloorReqClose {
    public long clubUid;
    public long uid;
    public int ownerType = 1;   // 拥有着类型, 1: 群, 2: 联盟

    @Override
    public String toString() {
        return "PCLIFloorReqClose{" +
                "clubUid=" + clubUid +
                ", uid=" + uid +
                ", ownerType=" + ownerType +
                '}';
    }
}
