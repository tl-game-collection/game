package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerReqSyncGps {
    public double lat;                                  // gps纬度
    public double lng;                                  // gps经度

    @Override
    public String toString() {
        return "PCLIPlayerReqSyncGps{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
