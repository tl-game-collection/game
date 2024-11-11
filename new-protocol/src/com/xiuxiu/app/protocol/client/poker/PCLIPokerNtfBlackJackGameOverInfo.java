package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomGameOverInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIPokerNtfBlackJackGameOverInfo extends PCLIRoomGameOverInfo {
    public static class GameOverInfo {
        public String name;                                         // 姓名
        public String icon;                                         // 图标
        public List<Byte> card = new ArrayList<>();                 // 手牌
        public int cardType;                                        // 牌型 0 没有牌型 1 BlackJack 2 五小龙
        public int points;                                          // 点数
        public String score;                                        // 本局积分
        public String totalScore;                                   // 总积分
        public TotalCnt totalCnt;                                   // 大结算计数
        public int scoreValue;                                      // 本局积分數
        public long playerUid;                                      // 玩家uid;
        public boolean isBust;                                      // 是否爆掉了

        @Override
        public String toString() {
            return "GameOverInfo{" +
                    "name='" + name + '\'' +
                    ", icon='" + icon + '\'' +
                    ", card=" + card +
                    ", cardType=" + cardType +
                    ", points=" + points +
                    ", score='" + score + '\'' +
                    ", totalScore='" + totalScore + '\'' +
                    ", totalCnt=" + totalCnt +
                    ", scoreValue=" + scoreValue +
                    ", playerUid=" + playerUid +
                    ", isBust=" + isBust +
                    '}';
        }
    }

    public static class TotalCnt {
        public int maxScore;            // 当局最高赢的分数
        public int maxCardType;         // 最大牌型
        public int winCnt;              // 赢的次数
        public int lostCnt;             // 输的次数


        @Override
        public String toString() {
            return "TotalCnt{" +
                    "maxScore=" + maxScore +
                    ", maxCardType=" + maxCardType +
                    ", winCnt=" + winCnt +
                    ", lostCnt=" + lostCnt +
                    '}';
        }
    }

    public HashMap<Long, GameOverInfo> allGameOverInfo = new HashMap<>();
    public List<Long> sortScorePlayerUidList = new ArrayList<>();                       // 玩家積分排名

    @Override
    public String toString() {
        return "PCLIPokerNtfBlackJackGameOverInfo{" +
                "allGameOverInfo=" + allGameOverInfo +
                ", sortScorePlayerUidList=" + sortScorePlayerUidList +
                ", bureau=" + bureau +
                ", roomType=" + roomType +
                ", next=" + next +
                '}';
    }
}