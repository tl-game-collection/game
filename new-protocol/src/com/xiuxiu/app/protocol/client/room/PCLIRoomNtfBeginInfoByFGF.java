package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomNtfBeginInfoByFGF extends PCLIRoomNtfBeginInfo {
    public int curLoop;             // 当前轮
    public int curBureau;                              // 局数

    @Override
    public String toString() {
        return "PCLIRoomNtfBeginInfoByFGF{" +
                "curBureau=" + curBureau +
                ", bankerIndex=" + bankerIndex +
                ", curLoop=" + curLoop +
                ", roomBriefInfo=" + roomBriefInfo +
                '}';
    }
}
