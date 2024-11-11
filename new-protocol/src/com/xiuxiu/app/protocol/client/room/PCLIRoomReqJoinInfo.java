package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomReqJoinInfo {
    public int roomId;
    public double lat;  // 纬度
    public double lng;  // 经度

    @Override
    public String toString() {
        return "PCLIRoomReqJoinInfo{" +
                "roomId=" + roomId +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
