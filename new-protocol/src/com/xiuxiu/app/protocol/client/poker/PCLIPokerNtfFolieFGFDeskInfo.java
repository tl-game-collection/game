package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomDeskInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIPokerNtfFolieFGFDeskInfo extends PCLIRoomDeskInfo  {
    public HashMap<Long, String> allScore = new HashMap<>();        // 所有人的分数
    public HashMap<Long, Integer> allNote = new HashMap<>();        // 所有人的下注
    public HashMap<Long, Boolean> allOnlineState = new HashMap<>(); // 所有人的在线状态
    public HashMap<Long, Integer> allState = new HashMap<>();       // 所有人的状态, 1: 弃牌, 2: 输牌, 0: 正常
    public HashMap<Long, Boolean> allLookCard = new HashMap<>();    // 所有人的看牌, true: 看牌, false: 未看牌
    public List<Byte> card = new ArrayList<>();                     // 自己的牌
    public int curLoop;                                             // 当前轮数
    public int remain = 0;                                          // 操作剩余时间
    public int curNote = 0;                                         // 当前底注
    public long curLoopOpId = -1;                                   // 当前操作的玩家ID

    @Override
    public String toString() {
        return "PCLIPokerNtfFolieFGFDeskInfo{" +
                "roomInfo=" + roomInfo +
                ", allScore=" + allScore +
                ", allNote=" + allNote +
                ", allOnlineState=" + allOnlineState +
                ", allState=" + allState +
                ", allLookCard=" + allLookCard +
                ", card=" + card +
                ", curBureau=" + curBureau +
                ", gameing=" + gameing +
                ", bankerIndex=" + bankerIndex +
                ", curLoop=" + curLoop +
                ", remain=" + remain +
                ", curNote=" + curNote +
                ", curLoopOpId=" + curLoopOpId +
                '}';
    }
}
