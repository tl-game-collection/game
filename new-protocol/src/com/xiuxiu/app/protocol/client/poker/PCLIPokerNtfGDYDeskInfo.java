package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomDeskInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIPokerNtfGDYDeskInfo extends PCLIRoomDeskInfo {
    public int curTakeIndex;                                        // 当前出牌索引
    public long lastTakePlayerUid;                                  //最后打出的牌玩家
    public List<Byte> lastTakeCard = new ArrayList();               // 最后打出的牌
    public List<Byte> card = new ArrayList();                       //当前手牌
    public HashMap<Long, String> allScore = new HashMap<>();        // 所有人的分数
    public HashMap<Long, Boolean> allOnlineState = new HashMap<>(); // 所有人的在线状态
    public HashMap<Long, Integer> otherCardCnt = new HashMap<>();   // 其他人的牌数
    public HashMap<Long, Integer> muls = new HashMap<>();           // 倍数
    public int boomScore;                                           // 炸弹
    public int turnCount;                                           // 轮数；
    public int leftCardSize;                                        // 剩余多少张牌

    @Override
    public String toString() {
        return "PCLIPokerNtfGDYDeskInfo{" +
                "roomInfo=" + roomInfo +
                ", curBureau=" + curBureau +
                ", gameing=" + gameing +
                ", bankerIndex=" + bankerIndex +
                ", curTakeIndex=" + curTakeIndex +
                ", lastTakePlayerUid=" + lastTakePlayerUid +
                ", lastTakeCard=" + lastTakeCard +
                ", card=" + card +
                ", allScore=" + allScore +
                ", allOnlineState=" + allOnlineState +
                ", otherCardCnt=" + otherCardCnt +
                ", muls=" + muls +
                ", boomScore=" + boomScore +
                ", turnCount=" + turnCount +
                ", leftCardSize=" + leftCardSize +
                '}';
    }
}
