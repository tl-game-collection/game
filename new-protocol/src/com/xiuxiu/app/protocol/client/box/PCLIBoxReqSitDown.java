package com.xiuxiu.app.protocol.client.box;

public class PCLIBoxReqSitDown {
    public long clubUid;            // 群uid
    public int roomId;              // 房间id
    public int index;                // 坐下位置索引

    @Override
    public String toString() {
        return "PCLIBoxReqSitDown{" +
                ", clubUid=" + clubUid +
                "roomId=" + roomId +
                ", index=" + index +
                '}';
    }
}
