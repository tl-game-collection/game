package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomDeskInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomPlayerInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PCLIPokerNtfPaiGowDeskInfo extends PCLIRoomDeskInfo {
    public int curTakeIndex;                                        // 当前出牌索引
    public long lastTakePlayerUid;                                  //最后打出的牌玩家
    public List<Byte> lastTakeCard = new ArrayList();               // 最后打出的牌
    public List<Byte> card = new ArrayList();                       //当前手牌
    public HashMap<Long, String> allScore = new HashMap<>();        // 所有人的分数
    public HashMap<Long, Boolean> allOnlineState = new HashMap<>(); // 所有人的在线状态

    public List<Byte> defaultCards = new ArrayList<>();     // 默认提示牌
    public int[] defaultType = new int[2];                  // 默认提示牌类型；
    public List<Byte> preDealCard = new ArrayList<>();      //一副牌中上一局发的牌
    public List<Byte> openCard = new ArrayList<>();
    public int[] openCardType = new int[2];

    public HashMap<Long, List<Integer>> allRebet = new HashMap<>();       // 所有人的下注
    public HashMap<Long, Integer> allRobBank = new HashMap<>();           // 所有人的抢庄
    public HashMap<Long, Boolean> isOpenCards = new HashMap<>();          //是否已经开牌
    public HashMap<Long, List<Byte>> openCards = new HashMap<>();

    public int curHotDeskNote;
    public int keepHotCount;
    public List<Byte> bankerShowCards = new ArrayList();
    public int bankerBureau;                                        // 加锅牌九，连庄局数

    public static class RoomPlayerInfo extends PCLIRoomPlayerInfo {
        public int arenaValue;

        @Override
        public String toString() {
            return "RoomPlayerInfo{" +
                    "arenaValue=" + arenaValue +
                    ", playerInfo=" + playerInfo +
                    ", index=" + index +
                    ", state=" + state +
                    ", guess=" + guess +
                    ", deskCard='" + deskCard + '\'' +
                    ", score='" + score + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PCLIPokerNtfPaiGowDeskInfo{" +
                "curTakeIndex=" + curTakeIndex +
                ", lastTakePlayerUid=" + lastTakePlayerUid +
                ", lastTakeCard=" + lastTakeCard +
                ", card=" + card +
                ", allScore=" + allScore +
                ", allOnlineState=" + allOnlineState +
                ", defaultCards=" + defaultCards +
                ", defaultType=" + Arrays.toString(defaultType) +
                ", preDealCard=" + preDealCard +
                ", openCard=" + openCard +
                ", openCardType=" + Arrays.toString(openCardType) +
                ", allRebet=" + allRebet +
                ", allRobBank=" + allRobBank +
                ", isOpenCards=" + isOpenCards +
                ", openCards=" + openCards +
                ", curHotDeskNote=" + curHotDeskNote +
                ", keepHotCount=" + keepHotCount +
                ", bankerShowCards=" + bankerShowCards +
                ", bankerBureau=" + bankerBureau +
                ", roomInfo=" + roomInfo +
                ", bankerPlayerUid=" + bankerPlayerUid +
                ", bankerIndex=" + bankerIndex +
                ", curBureau=" + curBureau +
                ", gameing=" + gameing +
                '}';
    }
}
