package com.xiuxiu.app.protocol.client.room;

import java.util.HashMap;

public class PCLIRoomReqCreateInfo {
    public long groupUid;       // 群Id
    public int gameType;        // 游戏类型
    public int gameSubType;     // 游戏子类中
    public HashMap<String, Integer> rule = new HashMap<>();
    public double lat;          // 纬度
    public double lng;          // 经度

    @Override
    public String toString() {
        return "PCLIRoomReqCreateInfo{" +
                "groupUid=" + groupUid +
                ", gameType=" + gameType +
                ", gameSubType=" + gameSubType +
                ", rule=" + rule +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
