package com.xiuxiu.app.protocol.client.robot;

public class PCLIRobotReqJoinInfo {
    public long clubUid;   // 群uid
    public int roomId;   // 房间号
    public double lat;      // 纬度
    public double lng;      // 经度

    @Override
    public String toString() {
        return "PCLIRobotReqJoinInfo{" +
                "clubUid=" + clubUid +
                ", roomId=" + roomId +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
