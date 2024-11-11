package com.xiuxiu.app.protocol.client.room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PCLIBoxRoomInfo {
    public int roomId;
    public int curBureau;
    public Map<String, Integer> rule = new HashMap<>();
    public List<PCLIRoomPlayerInfo> players = new ArrayList<>();
    public int gameType;                                            // 游戏类型
    public int gameSubType;                                         // 游戏子类型
    public Map<String, String> extra = new HashMap<>();
    public String remarks;// 备注
    
    @Override
    public String toString() {
        return "PCLIRoomInfo{" +
                "roomId=" + roomId +
                ", rule=" + rule +
                ", players=" + players +
                '}';
    }
}
