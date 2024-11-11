package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomGameOverInfo;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfCheLaKeGameOverInfo extends PCLIRoomGameOverInfo {
    public static class TotalCnt {
        public String maxScore;
        public int maxCardType;
        public int winCnt;
        public int lostCnt;

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

    public static class ShootList{
        public long playerUid; // 打枪玩家
        public long shootPlayerUid; // 被打枪玩家

        @Override
        public String toString() {
            return "ShootList{" +
                    "playerUid=" + playerUid +
                    ", shootPlayerUid=" + shootPlayerUid +
                    '}';
        }
    }

    public static class PlayerInfo {
        public static class PKWith {
            public long targetUid;
            public List<Integer> cardTypes = new ArrayList<>(3); // 牌型：头道、中道和尾道
            public List<Integer> cardScores = new ArrayList<>(3); // 每道牌对比结果，-1败，0平，1胜
            public int score; // 输赢得分，底分*倍数

            @Override
            public String toString() {
                return "PKWith{" +
                        "targetUid=" + targetUid +
                        ", cardTypes=" + cardTypes +
                        ", cardScores=" + cardScores +
                        ", score=" + score +
                        '}';
            }
        }

        public String name;         // 姓名
        public String icon;         // 图标
        public long playerUid;      // 玩家
        public String totalScore;   // 玩家累计总分
        public List<Byte> cards = new ArrayList<>();    // 13张，头道3张，中道5张，尾道5张
        public List<Integer> cardTypes = new ArrayList<>(3); // 牌型：头道、中道和尾道

        public int monsterType;                         // 拉克牌型，无为-1
        public boolean pronounced;                      // 是否宣告拉克
        public String bureauScore;                      // 当局分数
        public TotalCnt totalCnt;
        public List<PKWith> pkList = new ArrayList<>(4);

        public PKWith getPKWithPlayer(long targetUid) {
            for (PKWith pk : this.pkList) {
                if (pk.targetUid == targetUid) {
                    return pk;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return "PlayerInfo{" +
                    "name='" + name + '\'' +
                    ", icon='" + icon + '\'' +
                    ", playerUid=" + playerUid +
                    ", totalScore='" + totalScore + '\'' +
                    ", cards=" + cards +
                    ", cardTypes=" + cardTypes +
                    ", monsterType=" + monsterType +
                    ", pronounced=" + pronounced +
                    ", bureauScore='" + bureauScore + '\'' +
                    ", totalCnt=" + totalCnt +
                    ", pkList=" + pkList +
                    '}';
        }
    }

    public long passKillPlayerUid;           // 通杀玩家
    public List<ShootList> shootList = new ArrayList<>();
    public List<PlayerInfo> resultInfo = new ArrayList<>();

    public PlayerInfo getPlayerInfo(long playerUid) {
        for (PlayerInfo player : this.resultInfo) {
            if (player.playerUid == playerUid) {
                return player;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "PCLIPokerNtfCheLaKeResultInfo{" +
                "passKillPlayerUid=" + passKillPlayerUid +
                ", shootList=" + shootList +
                ", resultInfo=" + resultInfo +
                ", bureau=" + bureau +
                ", roomType=" + roomType +
                ", next=" + next +
                '}';
    }
}
