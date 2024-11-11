package com.xiuxiu.app.protocol.client.box;

import java.util.HashMap;

public class PCLIBoxInfo {
    public long boxUid;         // 包厢uid
    public long ownerUid;       // 群Id
    public long floorUid;       // 楼层uid
    public int boxType;         // 包厢类型, 0: 固定模式, 1: 自定义
    public String boxName;      // 包厢名字
    public int gameType;        // 游戏类型
    public int gameSubType;     // 游戏子类型
    public int index;           // 包厢索引, 楼成
    public int allPlayerCnt;    // 玩家数量
    public boolean bWaitClose;  // 是否待关闭
    public HashMap<String, Integer> rule = new HashMap<>();     // 规则
    public HashMap<String, String> extra = new HashMap<>();     // 额外参数

    @Override
    public String toString() {
        return "PCLIBoxInfo{" +
                "boxUid=" + boxUid +
                ", ownerUid=" + ownerUid +
                ", floorUid=" + floorUid +
                ", boxType=" + boxType +
                ", boxName='" + boxName + '\'' +
                ", gameType=" + gameType +
                ", gameSubType=" + gameSubType +
                ", index=" + index +
                ", allPlayerCnt=" + allPlayerCnt +
                ", bWaitClose=" + bWaitClose +
                ", rule=" + rule +
                ", extra=" + extra +
                '}';
    }
}
