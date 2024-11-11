package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomDeskInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIPokerNtfWuShiKDeskInfo extends PCLIRoomDeskInfo {
    public int bankerIndex;                                             // 庄家座位索引
    public int curTakeIndex;                                            // 当前出牌玩家座位索引
    public long lastTakePlayerUid;                                      // 最后打出的牌玩家UID
    public List<Byte> lastTakeCards = new ArrayList();                  // 最后打出的牌
    public List<Byte> cards = new ArrayList();                          // 当前手牌
    public int onDeskScore;                                             // 在桌子上的分数
    public HashMap<Long, String> score = new HashMap<>();               // 所有人总的分数
    public HashMap<Long, Integer> wskScore = new HashMap<>();          // 所有人当前局获得的牌分
    public HashMap<Long, Integer> boomScore = new HashMap<>();          // 所有人当前局获得的炸弹分
    public HashMap<Long, Boolean> onlineStates = new HashMap<>();       // 所有人的在线状态
    public HashMap<Long, Integer> playerCardCount = new HashMap<>();    // 玩家牌数
    public byte firstTakeCard;                                          // 明牌
    public long partnerUid;                                             // 2V2模式下，表示庄家盟友UID，-1表示尚未明鸡
    public List<Long> winPlayers = new ArrayList<>();                   // 已出完牌的玩家UID列表，依序排列
    public HashMap<Long, Boolean> autoMode = new HashMap<>();   // 是否托管
    @Override
    public String toString() {
        return "PCLIPokerNtfWuShiKDeskInfo{" +
                "bankerIndex=" + bankerIndex +
                ", curTakeIndex=" + curTakeIndex +
                ", lastTakePlayerUid=" + lastTakePlayerUid +
                ", lastTakeCards=" + lastTakeCards +
                ", cards=" + cards +
                ", onDeskScore=" + onDeskScore +
                ", score=" + score +
                ", wskScore=" + wskScore +
                ", boomScore=" + boomScore +
                ", onlineStates=" + onlineStates +
                ", playerCardCount=" + playerCardCount +
                ", firstTakeCard=" + firstTakeCard +
                ", partnerUid=" + partnerUid +
                ", winPlayers=" + winPlayers +
                ", roomInfo=" + roomInfo +
                ", bankerPlayerUid=" + bankerPlayerUid +
                ", bankerIndex=" + bankerIndex +
                ", curBureau=" + curBureau +
                ", autoMode=" + autoMode +
                ", gameing=" + gameing +
                '}';
    }
}
