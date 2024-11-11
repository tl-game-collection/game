package com.xiuxiu.app.protocol.client.box;

public class PCLIBoxReqSitUp {
    public long clubUid;            // 群uid
    public int roomId;             // 房间uid

    @Override
    public String toString() {
        return "PCLIBoxReqSitUp{" +
                "roomId=" + roomId +
                ", clubUid=" + clubUid +
                '}';
    }
}
