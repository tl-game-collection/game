package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomGameOverInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIPokerNtfSGGameOverInfo extends PCLIRoomGameOverInfo {
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
        public int cardTypeExtra;                                   //额外牌型

        @Override
        public String toString() {
            return "GameOverInfo{" +
                    "name='" + name + '\'' +
                    ", icon='" + icon + '\'' +
                    ", card=" + card +
                    ", cardType=" + cardType +
                    ", cardTypeExtra=" + cardTypeExtra +
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
         *     NONE("空", 0),
         *     SG_0DIAN("0点",1,1),
         *     SG_1DIAN("1点",2,1),
         *     SG_2DIAN("2点",3,1),
         *     SG_3DIAN("3点",4,1),
         *     SG_4DIAN("4点",5,1),
         *     SG_5DIAN("5点",6,1),
         *     SG_6DIAN("6点",7,1),
         *     SG_7DIAN("7点",8,2),
         *     SG_8DIAN("8点",9,3),
         *     SG_9DIAN("9点",10,4),
         *     SG_SG("三公",11,5),
         *     SG_MINSG("小三公",12,7),
         *     SG_MAXSG("大三公",13,8),
         *     SG_BAOJIU("暴玖",14,9),
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

    public int hotDeskNote;                                                             //端火锅桌面上的筹码

    @Override
    public String toString() {
        return "PCLIPokerNtfSGGameOverInfo{" +
                "allGameOverInfo=" + allGameOverInfo +
                ", sortScorePlayerUidList=" + sortScorePlayerUidList +
                ", hotDeskNote=" + hotDeskNote +
                ", bureau=" + bureau +
                ", roomType=" + roomType +
                ", next=" + next +
                '}';
    }
}