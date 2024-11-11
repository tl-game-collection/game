package com.xiuxiu.app.protocol.client.poker.cow;

import com.xiuxiu.app.protocol.client.room.PCLIRoomGameOverInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 斗公牛小结算消息对象
 * @auther: yuyunfei
 * @date: 2020/1/7 9:56
 * @comment:
 */
public class PCLIPokerNtfCowGameOverInfo extends PCLIRoomGameOverInfo {
    public static class GameOverInfo {
        public String name;                                         // 姓名
        public String icon;                                         // 图标
        public List<Byte> card = new ArrayList<>();                 // 手牌
        public int cardType;                                        // 牌型
        public String score;                                        // 本局积分
        public String totalScore;                                   // 总积分
        public TotalCnt totalCnt;                                   // 大结算计数
        public int cardDouble;                                      //特殊玩法牛的倍数
        public int robBankerMul;                                     //抢庄倍数
        public int scoreValue;                                      // 本局积分數
        public long playerUid;                                      //玩家uid；
        public byte lastCardValue = -1;                             // 最后一张牌的值
        public List<Byte> handCard = new ArrayList<>();            // 玩家初始牌

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
                    ", cardDouble=" + cardDouble +
                    ", robBankerMul=" + robBankerMul +
                    ", scoreValue=" + scoreValue +
                    ", playerUid=" + playerUid +
                    ", lastCardValue=" + lastCardValue +
                    '}';
        }
    }

    public static class TotalCnt {
        public int maxScore;            // 当局最高赢的分数
        /**
         *     COW_NONE("无牛", 12),
         *     COW_1("牛1", 13),
         *     COW_2("牛2", 14),
         *     COW_3("牛3", 15),
         *     COW_4("牛4", 16),
         *     COW_5("牛5", 17),
         *     COW_6("牛6", 18),
         *     COW_7("牛7", 19),
         *     COW_8("牛8", 20),
         *     COW_9("牛9", 21),
         *     COW_10("牛牛", 22),
         *     COW_STRAIGHT("牛-顺子牛", 23),
         *     COW_SILVER("牛-银牛", 24),
         *     COW_SAME_COLOR("牛-同花牛", 25),
         *     COW_GOLD("牛-金牛", 26),
         *     COW_CUCURBIT("牛-葫芦牛", 27),
         *     COW_FIVE_SMALL("牛-五小牛", 28),
         *     COW_BOMB("牛-炸弹牛", 29),
         *     COW_DRAGON("牛-一条龙", 30),
         *     COW_WITH_THE_FLOWER("牛-同花顺", 31),
         */
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


    public int hotDeskNote; //端火锅桌面上的筹码
    public int bankerBureau;//当前庄已经进行了多少轮
    public int keepHotCount;//当前庄续了几次

    @Override
    public String toString() {
        return "PCLIPokerNtfCowGameOverInfo{" +
                "allGameOverInfo=" + allGameOverInfo +
                ", sortScorePlayerUidList=" + sortScorePlayerUidList +
                ", hotDeskNote=" + hotDeskNote +
                ", bureau=" + bureau +
                ", roomType=" + roomType +
                ", next=" + next +
                '}';
    }
}
