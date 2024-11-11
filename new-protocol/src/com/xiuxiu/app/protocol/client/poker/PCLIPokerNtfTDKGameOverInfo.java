package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomGameOverInfo;

import java.util.List;

public class PCLIPokerNtfTDKGameOverInfo extends PCLIRoomGameOverInfo {
    public long timestamp;
    public List<PlayerInfo> players;

    public static class PlayerInfo {
        public long uid;
        public String name;
        public String icon;
        public List<Byte> cards;        // 手牌
        public int cardType;            // 牌型，3-3条，4-4条，其他-杂牌
        public int cardScore;           // 牌分，为0时代表该玩家弃牌
        public int score;               // 本局积分
        public int totalScore;          // 总积分
        public TotalCnt totalCnt;       // 大结算计数

        @Override
        public String toString() {
            return "PlayerInfo{" +
                    "uid=" + uid +
                    ", name='" + name + '\'' +
                    ", icon='" + icon + '\'' +
                    ", cards=" + cards +
                    ", cardType=" + cardType +
                    ", cardScore=" + cardScore +
                    ", score=" + score +
                    ", totalScore=" + totalScore +
                    ", totalCnt=" + totalCnt +
                    '}';
        }
    }

    public static class TotalCnt {
        public int winCnt;
        public int lostCnt;
        public int maxScore;

        @Override
        public String toString() {
            return "TotalCnt{" +
                    "winCnt=" + winCnt +
                    ", lostCnt=" + lostCnt +
                    ", maxScore=" + maxScore +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PCLIPokerNtfTDKGameOverInfo{" +
                "timestamp=" + timestamp +
                ", players=" + players +
                ", bureau=" + bureau +
                ", roomType=" + roomType +
                ", next=" + next +
                '}';
    }
}
