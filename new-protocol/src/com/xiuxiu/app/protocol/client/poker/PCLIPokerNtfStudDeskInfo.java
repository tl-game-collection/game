package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomDeskInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomPlayerInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIPokerNtfStudDeskInfo extends PCLIRoomDeskInfo {
    public List<Byte> cards = new ArrayList<>();                    // 当前手牌
    public HashMap<Long, String> scores = new HashMap<>();          // 所有人总的分数
    public HashMap<Long, Integer> curScores = new HashMap<>();      // 所有人当前局获得的牌分
    public HashMap<Long, Boolean> onlineStates = new HashMap<>();   // 所有人的在线状态
    public long timeout;

    public int pot; // 底池
    public Round round = new Round();

    public static class RoomPlayerInfo extends PCLIRoomPlayerInfo {
        public int status; // 当前状态，1-正常，5-已梭哈，6-已弃牌
        public int bankRoll; // 剩余筹码数量（过渡，请使用stakes）
        public int bet; // 该局游戏中总下注额度
        public List<Byte> openCards = new ArrayList<>(); // 明牌
        public int stakes; // 剩余筹码数量
        public boolean autoFillUpStakes; // 是否开启自动补充筹码

        @Override
        public String toString() {
            return "RoomPlayerInfo{" +
                    "status=" + status +
                    ", bankRoll=" + bankRoll +
                    ", bet=" + bet +
                    ", openCards=" + openCards +
                    ", stakes=" + stakes +
                    ", autoFillUpStakes=" + autoFillUpStakes +
                    ", playerInfo=" + playerInfo +
                    ", index=" + index +
                    ", state=" + state +
                    ", guess=" + guess +
                    ", deskCard='" + deskCard + '\'' +
                    ", score='" + score + '\'' +
                    '}';
        }
    }

    public static class Round {
        public int round; // 当前轮次
        public long playerUid; // 轮到谁下注
        public long bossUid;
        public int bossBetType;
        public int bossBet;

        @Override
        public String toString() {
            return "Round{" +
                    "round=" + round +
                    ", playerUid=" + playerUid +
                    ", bossUid=" + bossUid +
                    ", bossBetType=" + bossBetType +
                    ", bossBet=" + bossBet +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PCLIPokerNtfStudDeskInfo{" +
                "cards=" + cards +
                ", scores=" + scores +
                ", curScores=" + curScores +
                ", onlineStates=" + onlineStates +
                ", timeout=" + timeout +
                ", pot=" + pot +
                ", round=" + round +
                ", roomInfo=" + roomInfo +
                ", bankerPlayerUid=" + bankerPlayerUid +
                ", bankerIndex=" + bankerIndex +
                ", curBureau=" + curBureau +
                ", gameing=" + gameing +
                '}';
    }
}
