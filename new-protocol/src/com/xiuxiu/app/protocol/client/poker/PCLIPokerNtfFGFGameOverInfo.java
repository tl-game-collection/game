package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomGameOverInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIPokerNtfFGFGameOverInfo extends PCLIRoomGameOverInfo {
    public static class GameOverInfo {
        public String name;                                     // 姓名
        public String icon;                                     // 图标
        public List<Byte> card = new ArrayList<>();                 // 手牌
        /**
         *     FGF_NONE("扎金花-高牌", 32),
         *     FGF_235("扎金花-高牌", 33),
         *     FGF_DOUBLE("扎金花--对子", 34),
         *     FGF_LINE("扎金花--顺子", 35),
         *     FGF_SAME_COLOR("扎金花--同花", 36),
         *     FGF_SAME_COLOR_AND_LINE("扎金花--同花顺", 37),
         *     FGF_THREE("扎金花--豹子", 38),
         */
        public int cardType;                                        // 牌型
        public String score;                                        // 本局积分
        public String totalScore;                                   // 总积分
        public TotalCnt totalCnt;                                   // 大结算计数

        @Override
        public String toString() {
            return "GameOverInfo{" +
                    "name='" + name + '\'' +
                    ", icon='" + icon + '\'' +
                    ", card=" + card +
                    ", cardType=" + cardType +
                    ", score='" + score + '\'' +
                    ", totalScore='" + totalScore + '\'' +
                    ", totalCnt=" + totalCnt +
                    '}';
        }
    }

    public static class TotalCnt {
        public int winCnt;              // 赢的次数
        public int lostCnt;             // 输的次数
        public int maxScore;            // 单局最大积分
        public int maxCardType;         // 单局最大牌型

        @Override
        public String toString() {
            return "TotalCnt{" +
                    "winCnt=" + winCnt +
                    ", lostCnt=" + lostCnt +
                    ", maxScore=" + maxScore +
                    ", maxCardType=" + maxCardType +
                    '}';
        }
    }


    public HashMap<Long, GameOverInfo> allGameOverInfo = new HashMap<>();
    public List<Long> threePlayerList = new ArrayList<>();      // 豹子玩家Uid
    public long winPlayerUid;                                   // 赢家玩家uid

    @Override
    public String toString() {
        return "PCLIPokerNtfFGFGameOverInfo{" +
                "allGameOverInfo=" + allGameOverInfo +
                ", threePlayerList=" + threePlayerList +
                ", winPlayerUid=" + winPlayerUid +
                ", bureau=" + bureau +
                ", roomType=" + roomType +
                ", next=" + next +
                '}';
    }
}
