package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomGameOverInfo;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfTexasGameOverInfo extends PCLIRoomGameOverInfo {
    public List<PlayerInfo> resultInfo = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIPokerNtfTexasGameOverInfo{" +
                "resultInfo=" + resultInfo +
                ", bureau=" + bureau +
                ", roomType=" + roomType +
                ", next=" + next +
                '}';
    }

    public PlayerInfo getPlayerInfo(long playerUid) {
        for (PlayerInfo player : this.resultInfo) {
            if (player.playerUid == playerUid) {
                return player;
            }
        }
        return null;
    }

    public static class TotalCnt {
        public int maxScore;
        public int maxCardType;
        public int winCnt;
        public int lostCnt;

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

    public static class PlayerInfo {
        public String name; // 姓名
        public String icon; // 图标
        public long playerUid; // 玩家
        public int status; // 最终状态，1-正常，5-已梭哈，6-已弃牌
        public List<Byte> cards = new ArrayList<>(); // 手牌
        public int cardType; // 牌型
        public int bureauScore; // 本局分数
        public int totalScore; // 总分
        public int bankRoll; // 筹码数量
        public TotalCnt totalCnt;
        public List<Byte> composeCards = new ArrayList<>(); //组合手牌
        @Override
        public String toString() {
            return "PlayerInfo{" +
                    "name='" + name + '\'' +
                    ", icon='" + icon + '\'' +
                    ", playerUid=" + playerUid +
                    ", status=" + status +
                    ", cards=" + cards +
                    ", cardType=" + cardType +
                    ", bureauScore=" + bureauScore +
                    ", totalScore=" + totalScore +
                    ", bankRoll=" + bankRoll +
                    ", totalCnt=" + totalCnt +
                    ", composeCards=" + composeCards +
                    '}';
        }
    }
}
