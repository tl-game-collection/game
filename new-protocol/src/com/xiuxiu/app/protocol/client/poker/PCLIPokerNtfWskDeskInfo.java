package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomDeskInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIPokerNtfWskDeskInfo extends PCLIRoomDeskInfo {
    public List<Byte> card = new ArrayList<>();                     // 自己的牌
    public HashMap<Long, Integer> otherCardCnt = new HashMap<>();   // 其他人的牌数
    public HashMap<Long, String> allScore = new HashMap<>();       // 所有人的分数
    public HashMap<Long, Boolean> allOnlineState = new HashMap<>(); // 所有人的在线状态
    public List<Byte> lastTakeCard;                                 // 最后打出的牌
    public long lastTakePlayerUid;                                  // 最后打出的牌玩家uid
    public int onDeskScore;                                         //在桌子上的分数；

    @Override
    public String toString() {
        return "PCLIPokerNtfWskDeskInfo{" +
                "card=" + card +
                ", otherCardCnt=" + otherCardCnt +
                ", allScore=" + allScore +
                ", allOnlineState=" + allOnlineState +
                ", lastTakeCard=" + lastTakeCard +
                ", lastTakePlayerUid=" + lastTakePlayerUid +
                ", onDeskScore=" + onDeskScore +
                ", roomInfo=" + roomInfo +
                ", bankerPlayerUid=" + bankerPlayerUid +
                ", bankerIndex=" + bankerIndex +
                ", curBureau=" + curBureau +
                ", gameing=" + gameing +
                '}';
    }
}
