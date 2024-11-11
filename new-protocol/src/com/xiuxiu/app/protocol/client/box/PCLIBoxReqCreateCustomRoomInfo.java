package com.xiuxiu.app.protocol.client.box;

import java.util.HashMap;

public class PCLIBoxReqCreateCustomRoomInfo {
    public long clubUid;       // 群uid
    public long boxUid;         // 包厢uid
    public int gameType;        // 游戏类型
    public int gameSubType;     // 游戏子类型
    public int roomIndex;       // 房间索引
    public HashMap<String, Integer> rule = new HashMap<>();  // 规则, 根据配置表来
    public double lat;          // 纬度
    public double lng;          // 经度
    public String remarks;

    @Override
    public String toString() {
        return "PCLIBoxReqCreateCustomRoomInfo{" +
                "clubUid=" + clubUid +
                ", boxUid=" + boxUid +
                ", gameType=" + gameType +
                ", gameSubType=" + gameSubType +
                ", roomIndex=" + roomIndex +
                ", rule=" + rule +
                ", lat=" + lat +
                ", lng=" + lng +
                ", remarks=" + remarks +
                '}';
    }
}
