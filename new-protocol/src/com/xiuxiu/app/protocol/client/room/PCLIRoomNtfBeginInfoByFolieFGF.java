package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomNtfBeginInfoByFolieFGF extends PCLIRoomNtfBeginInfo {
    public int curLoop;             // 当前轮
    public int bankRoll;
    public int myIndex;
    public int pot;                 // 桌上的筹码

    @Override
    public String toString() {
        return "PCLIRoomNtfBeginInfoByFolieFGF{" +
                "bureau=" + bureau +
                ",myIndex" + myIndex +
                ", bankerIndex=" + bankerIndex +
                ", curLoop=" + curLoop +
                ", bankRoll=" + bankRoll +
                ", pot=" + pot +
                ", roomBriefInfo=" + roomBriefInfo +
                '}';
    }
}
