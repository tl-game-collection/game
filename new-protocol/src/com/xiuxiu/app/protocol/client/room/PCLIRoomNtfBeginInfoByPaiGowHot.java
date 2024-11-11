package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomNtfBeginInfoByPaiGowHot extends PCLIRoomNtfBeginInfo {
    public int loop;
    public int hotNote;

    @Override
    public String toString() {
        return "PCLIRoomNtfBeginInfoByPaiGowHot{" +
                "loop=" + loop +
                ", hotNote=" + hotNote +
                ", bankerIndex=" + bankerIndex +
                ", bureau=" + bureau +
                ", roomBriefInfo=" + roomBriefInfo +
                ", d=" + d +
                '}';
    }
}
