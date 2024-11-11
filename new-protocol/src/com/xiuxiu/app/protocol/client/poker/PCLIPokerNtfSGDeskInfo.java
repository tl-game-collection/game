package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomDeskInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIPokerNtfSGDeskInfo extends PCLIRoomDeskInfo {
    public HashMap<Long, String> allScore = new HashMap<>();        // 所有人的分数
    public HashMap<Long, Boolean> allOnlineState = new HashMap<>(); // 所有人的在线状态
    public HashMap<Long, Integer> allRebet = new HashMap<>();       // 所有人的下注
    public HashMap<Long, Integer> allRobBank = new HashMap<>();     // 所有人的抢庄
    public HashMap<Long, Boolean> isLookCard = new HashMap<>();     // 当前玩家是否已经看牌
    public HashMap<Long, Integer> pushNoteScore = new HashMap<>();  // 所有文件推注倍数
    public List<Byte> card = new ArrayList<>();                     // 自己的牌
    public int sendCardCount;

    @Override
    public String toString() {
        return "PCLIPokerNtfSGDeskInfo{" +
                "roomInfo=" + roomInfo +
                ", allScore=" + allScore +
                ", allOnlineState=" + allOnlineState +
                ", allRebet=" + allRebet +
                ", allRobBank=" + allRobBank +
                ", isLookCard=" + isLookCard +
                ", pushNoteScore=" + pushNoteScore +
                ", card=" + card +
                ", curBureau=" + curBureau +
                ", gameing=" + gameing +
                ", bankerIndex=" + bankerIndex +
                ", sendCardCount=" + sendCardCount +
                '}';
    }
}
