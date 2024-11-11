package com.xiuxiu.app.protocol.client.player;

import java.util.ArrayList;
import java.util.List;

public class PCLIPlayerReqPostTrends {
    public String content;
    public List<String> images;
    public byte lookType;                               // 0: 公开, 1: 只有好友可看, 2: 只有附近可看, 3: 指定好友可看, 4: 指定好友不可看
    public List<Long> playerUids = new ArrayList<>();
    public boolean showLocation;                        // 是否公开位置
    public double lat;                                  // gps纬度
    public double lng;                                  // gps经度

    @Override
    public String toString() {
        return "PCLIPlayerReqPostTrends{" +
                "content='" + content + '\'' +
                ", images=" + images +
                ", lookType=" + lookType +
                ", playerUids=" + playerUids +
                ", showLocation=" + showLocation +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
