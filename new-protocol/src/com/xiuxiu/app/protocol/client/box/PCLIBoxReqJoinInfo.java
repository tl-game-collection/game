package com.xiuxiu.app.protocol.client.box;

public class PCLIBoxReqJoinInfo {
    public long clubUid;   // 群uid
    public long boxUid;     // 包厢uid
    public int roomIndex;   // 房间索引, -1: 快速加入
    public double lat;      // 纬度
    public double lng;      // 经度

    @Override
    public String toString() {
        return "PCLIBoxReqJoinInfo{" +
                "clubUid=" + clubUid +
                ", boxUid=" + boxUid +
                ", roomIndex=" + roomIndex +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
