package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomDeskInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIPokerNtfArchDeskInfo extends PCLIRoomDeskInfo {
    public int bankerIndex;                                         // 庄家座位索引
    public int curTakeIndex;                                        // 当前出牌玩家座位索引
    public int contract;                                            // 定约
    public long lastTakePlayerUid;                                  // 最后打出的牌玩家UID
    public List<Byte> lastTakeCards = new ArrayList();              // 最后打出的牌
    public List<Byte> lastTakeLaizi = new ArrayList();              // 最后打出的癞子牌
    public List<Byte> cards = new ArrayList();                      // 当前手牌
    public List<Byte> reservedCards = new ArrayList();              // 底牌
    public int onDeskScore;                                         // 在桌子上的分数
    public HashMap<Long, String> scores = new HashMap<>();          // 所有人总的分数
    public HashMap<Long, Integer> curScores = new HashMap<>();      // 所有人当前局获得的牌分
    public HashMap<Long, Boolean> onlineStates = new HashMap<>();   // 所有人的在线状态
    public HashMap<Long, Integer> playerCardCount = new HashMap<>(); // 玩家牌数
    public byte firstTakeCard;                                      // 明牌
    public long bankerPartnerUid;                                   // 2V2模式下，表示庄家盟友UID，-1表示尚未明鸡
    public List<Long> winners = new ArrayList<>();                  // 已出完牌的玩家UID列表，依序排列
    public List<Byte> laizi = new ArrayList();                      // 癞子牌
    public HashMap<Long, Boolean> autoMode = new HashMap<>();   // 是否托管

    @Override
    public String toString() {
        return "PCLIPokerNtfArchDeskInfo{" +
                "bankerIndex=" + bankerIndex +
                ", curTakeIndex=" + curTakeIndex +
                ", contract=" + contract +
                ", lastTakePlayerUid=" + lastTakePlayerUid +
                ", lastTakeCards=" + lastTakeCards +
                ", lastTakeLaizi=" + lastTakeLaizi +
                ", cards=" + cards +
                ", reservedCards=" + reservedCards +
                ", onDeskScore=" + onDeskScore +
                ", scores=" + scores +
                ", curScores=" + curScores +
                ", onlineStates=" + onlineStates +
                ", playerCardCount=" + playerCardCount +
                ", firstTakeCard=" + firstTakeCard +
                ", bankerPartnerUid=" + bankerPartnerUid +
                ", winners=" + winners +
                ", laizi=" + laizi +
                ", roomInfo=" + roomInfo +
                ", bankerPlayerUid=" + bankerPlayerUid +
                ", bankerIndex=" + bankerIndex +
                ", curBureau=" + curBureau +
                ", autoMode=" + autoMode +
                ", gameing=" + gameing +
                '}';
    }
}
