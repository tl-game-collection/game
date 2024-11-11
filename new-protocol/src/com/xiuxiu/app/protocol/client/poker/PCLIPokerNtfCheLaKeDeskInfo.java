package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomDeskInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIPokerNtfCheLaKeDeskInfo extends PCLIRoomDeskInfo {
    public List<Byte> cards = new ArrayList();                      // 当前手牌
    public HashMap<Long, String> scores = new HashMap<>();          // 所有人的分数
    public HashMap<Long, Boolean> onlineStates = new HashMap<>();   // 所有人的在线状态
    public List<Long> allTakeCard = new ArrayList<>();              // 已出牌玩家
    public HashMap<Long, Integer> playerCardCount = new HashMap<>(); // 玩家牌数
    public byte firstTakeCard;                                      // 明牌

    @Override
    public String toString() {
        return "PCLIPokerNtfCheLaKeDeskInfo{" +
                "cards=" + cards +
                ", scores=" + scores +
                ", onlineStates=" + onlineStates +
                ", allTakeCard=" + allTakeCard +
                ", playerCardCount=" + playerCardCount +
                ", firstTakeCard=" + firstTakeCard +
                ", roomInfo=" + roomInfo +
                ", bankerPlayerUid=" + bankerPlayerUid +
                ", bankerIndex=" + bankerIndex +
                ", curBureau=" + curBureau +
                ", gameing=" + gameing +
                '}';
    }
}
