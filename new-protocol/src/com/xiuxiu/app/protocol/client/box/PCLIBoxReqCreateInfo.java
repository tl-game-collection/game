package com.xiuxiu.app.protocol.client.box;

import java.util.HashMap;

public class PCLIBoxReqCreateInfo {
    public int boxType;         // 包厢类型, 0: 固定模式, 1: 自定义
    public long clubUid;       // 群Id
    public long floorUid;       // 楼层uid
    public int gameType;        // 游戏类型
    public int gameSubType;     // 游戏子类型
    public int ownerType;       //拥有者类型
    public HashMap<String, Integer> rule = new HashMap<>();  // 规则, 根据配置表来
    public HashMap<String, String> extra = new HashMap<>();  // 额外, 根据配置表来

    @Override
    public String toString() {
        return "PCLIBoxReqCreateInfo{" +
                "boxType=" + boxType +
                ", clubUid=" + clubUid +
                ", floorUid=" + floorUid +
                ", gameType=" + gameType +
                ", gameSubType=" + gameSubType +
                ", ownerType=" + ownerType +
                ", rule=" + rule +
                ", extra=" + extra +
                '}';
    }
}
