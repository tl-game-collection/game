package com.xiuxiu.app.protocol.client.room;

import java.util.HashMap;

public class PCLIRoomBriefInfo {
    public int roomId;
    public int roomType;                                            // 房间类型, 0: 普通房间, 1: 竞技场房间, 2: 比赛场房间, 3: 包厢
    public int gameType;                                            // 游戏类型
    public int gameSubType;                                         // 游戏子类型
    public HashMap<String, Integer> rule = new HashMap<>();         // 规则
    public int state;                                               // 1:可加入，2:已结束，3:已满，4:已开始
    public int curBureau;                                           // 当前局数
    public boolean gameing;                                         // 游戏中
    public long timeout;                                            // 房间超时设置
    public int ownerType;                                           // 联盟标识 0 普通房间 1 群竞技场 2 联盟竞技场

    @Override
    public String toString() {
        return "PCLIRoomBriefInfo{" +
                "roomId=" + roomId +
                ", roomType=" + roomType +
                ", gameType=" + gameType +
                ", ownerType=" + ownerType +
                ", gameSubType=" + gameSubType +
                ", rule=" + rule +
                ", state=" + state +
                ", curBureau=" + curBureau +
                ", gameing=" + gameing +
                ", timeout=" + timeout +
                '}';
    }
}
