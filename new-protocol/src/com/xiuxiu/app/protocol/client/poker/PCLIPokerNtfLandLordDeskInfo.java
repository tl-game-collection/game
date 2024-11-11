package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomDeskInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PCLIPokerNtfLandLordDeskInfo extends PCLIRoomDeskInfo {
    public int curTakeIndex;                                        // 当前出牌索引
    public int maxScore;                                            // 叫分
    public long lastTakePlayerUid;                                  // 最后打出的牌玩家
    public List<Byte> lastTakeCard = new ArrayList();               // 最后打出的牌
    public List<Byte> lastTakeLaiziCard = new ArrayList();          // 最后打出的癞子牌
    public List<Byte> card = new ArrayList();                       // 当前手牌
    public List<Byte> lastCard = new ArrayList();                   // 底牌信息
    public HashMap<Long, List<Byte>> allShowCards = new HashMap<>();// 明牌信息
    public HashMap<Long, String> allScore = new HashMap<>();        // 所有人的分数
    public HashMap<Long, Boolean> allOnlineState = new HashMap<>(); // 所有人的在线状态
    public HashMap<Long, Integer> otherCardCnt = new HashMap<>();   // 其他人的牌数
    public HashMap<Long, Integer> multiples = new HashMap<>();      // 加倍信息，1-为加倍，2-2倍，4-4倍
    public List<Byte> laiziCard = new ArrayList();                  // 癞子牌
    public int boomScore;                                           // 炸弹倍数
    public HashMap<Long, Boolean> autoMode = new HashMap<>();   // 是否托管
    public HashMap<Long,Map<String,Boolean>> kick= new HashMap<>(); //  kickSingle 踢 ,kickTogether 一起踢,   回踢 kickBack  
    public long timeout;
    @Override
    public String toString() {
        return "PCLIPokerNtfLandLordDeskInfo{" +
                "curTakeIndex=" + curTakeIndex +
                ", maxScore=" + maxScore +
                ", lastTakePlayerUid=" + lastTakePlayerUid +
                ", lastTakeCard=" + lastTakeCard +
                ", lastTakeLaiziCard=" + lastTakeLaiziCard +
                ", card=" + card +
                ", lastCard=" + lastCard +
                ", allShowCards=" + allShowCards +
                ", allScore=" + allScore +
                ", allOnlineState=" + allOnlineState +
                ", otherCardCnt=" + otherCardCnt +
                ", multiples=" + multiples +
                ", laiziCard=" + laiziCard +
                ", boomScore=" + boomScore +
                ", roomInfo=" + roomInfo +
                ", bankerPlayerUid=" + bankerPlayerUid +
                ", bankerIndex=" + bankerIndex +
                ", curBureau=" + curBureau +
                ", autoMode=" + autoMode +
                ", gameing=" + gameing +
                '}';
    }
}
