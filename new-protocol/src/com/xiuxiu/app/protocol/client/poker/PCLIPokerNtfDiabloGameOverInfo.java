package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomGameOverInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIPokerNtfDiabloGameOverInfo extends PCLIRoomGameOverInfo {
    public static class BoutInfo {
        public int bout;        //此道序列，1头道 2中道 3尾道
        public int rank;        //此道排名
        public String score;       //此道分数
        public int cardType;    //此道牌型

        @Override
        public String toString() {
            return "boutInfo{" +
                    "bout=" + bout +
                    ", rank='" + rank + '\'' +
                    ", score='" + score + '\'' +
                    ", cardType=" + cardType +
                    '}';
        }
    }

    public static class TotalCnt {
        public String maxScore;
        public int maxCardType;
        public int winCnt;
        public int lostCnt;

        public TotalCnt() {
        }

        @Override
        public String toString() {
            return "TotalCnt{" +
                    "maxScore='" + maxScore + '\'' +
                    ", maxCardType=" + maxCardType +
                    ", winCnt=" + winCnt +
                    ", lostCnt=" + lostCnt +
                    '}';
        }
    }

    public static class PlayerInfo {
        public String name;         // 姓名
        public String icon;         // 图标
        public long playerUid;      //玩家
        public String totalScore;      //玩家累计总分
        public List<Byte> cards = new ArrayList<>();    // 13张，头墩3张，中墩5张，尾墩5张
        public List<BoutInfo> boutInfo = new ArrayList<>(3);
        public int monsterType;                         // 怪物牌型，无为0
        public String monsterScore;                        // 怪物分数;
        public String shootScore;                          // 打枪分数;
        public String horseScore;                          // 马牌分数;
        public boolean hasHorseCard;                    // 是否有马牌;
        public List<Long> shootPlayerList = new ArrayList<>(); // 打枪玩家列表;
        public String bureauScore;                         // 当局分数;
        public TotalCnt totalCnt;

        @Override
        public String toString() {
            return "PlayerInfo{" +
                    "name='" + name + '\'' +
                    ", icon='" + icon + '\'' +
                    ", playerUid=" + playerUid +
                    ", totalScore='" + totalScore + '\'' +
                    ", cards=" + cards +
                    ", boutInfo=" + boutInfo +
                    ", monsterType=" + monsterType +
                    ", monsterScore='" + monsterScore + '\'' +
                    ", shootScore='" + shootScore + '\'' +
                    ", horseScore='" + horseScore + '\'' +
                    ", hasHorseCard=" + hasHorseCard +
                    ", shootPlayerList=" + shootPlayerList +
                    ", bureauScore='" + bureauScore + '\'' +
                    ", totalCnt=" + totalCnt +
                    '}';
        }
    }

    public static class ShootList {
        public long playerUid;           //打枪玩家
        public long shootPlayerUid;      //被打枪玩家

        @Override
        public String toString() {
            return "ShootList{" +
                    "playerUid=" + playerUid +
                    ", shootPlayerUid=" + shootPlayerUid +
                    '}';
        }
    }

    public HashMap<Long/*playerUid*/, Integer/*type*/> monsterRank = new HashMap<>();
    public long passKillPlayerUid;           //通杀玩家
    public List<ShootList> shootList = new ArrayList<>();
    public List<PlayerInfo> resultInfo = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIPokerNtfDiabloResult{" +
                "monsterRank=" + monsterRank +
                ", passKillPlayerUid=" + passKillPlayerUid +
                ", shootList=" + shootList +
                ", resultInfo=" + resultInfo +
                ", next=" + next +
                '}';
    }
}
