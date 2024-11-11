package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomNtfBeginInfo {
    public int bankerIndex;                         // 庄家
    public int bureau;                              // 局数
    public PCLIRoomBriefInfo roomBriefInfo;         // 房间信息
    public boolean d = false;                       // 调试信息

    @Override
    public String toString() {
        return "PCLIRoomNtfBeginInfo{" +
                "bankerIndex=" + bankerIndex +
                ", bureau=" + bureau +
                ", roomBriefInfo=" + roomBriefInfo +
                ", d=" + d +
                '}';
    }
}
