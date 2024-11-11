package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.List;

public class PCLIMahjongNtfGameOverInfoByDYMJ extends PCLIMahjongNtfGameOverInfo {
    public static class ScoreInfo extends PCLIMahjongNtfGameOverInfo.ScoreInfo {

        @Override
        public String toString() {
            return "ScoreInfo{" +
                    "barCnt=" + barCnt +
                    ", gangScore=" + gangScore +
                    ", fangScore=" + fangScore +
                    ", huScore=" + huScore +
                    ", score='" + score + '\'' +
                    ", totalScore='" + totalScore + '\'' +
                    '}';
        }
    }

    public static class PlayerInfo extends PCLIMahjongNtfGameOverInfo.PlayerInfo {

        @Override
        public String toString() {
            return "PlayerInfo{" +
                    "score=" + score +
                    ", finalResult=" + finalResult +
                    ", handCard=" + handCard +
                    ", allHuInfo=" + allHuInfo +
                    ", allShow=" + allShow +
                    '}';
        }
    }

    public boolean isSmallGold = false;             // 小金顶
    public int smallGoldPoint1 = 0;                 // 小金顶点数1
    public int smallGoldPoint2 = 0;                 // 小金顶点数2
    public boolean isBaoZiF = false;                // 豹子翻倍
    public boolean isJFYLF = false;                 // 见风原癞翻倍
    public boolean isJ258F = false;                 // 见258将翻倍
    public List<Byte> haiDiLaoCard = new ArrayList<>(); // 海底捞摸的牌
    public long haiDiLaoStartPlayerUid = -1;        // 海底捞开始摸牌的玩家uid

    @Override
    public String toString() {
        return "PCLIMahjongNtfGameOverInfoByWHMJ{" +
                "isSmallGold=" + isSmallGold +
                ", smallGoldPoint1=" + smallGoldPoint1 +
                ", smallGoldPoint2=" + smallGoldPoint2 +
                ", isBaoZiF=" + isBaoZiF +
                ", isJFYLF=" + isJFYLF +
                ", isJ258F=" + isJ258F +
                ", haiDiLaoCard=" + haiDiLaoCard +
                ", haiDiLaoStartPlayerUid=" + haiDiLaoStartPlayerUid +
                ", allPlayer=" + allPlayer +
                ", bureau=" + bureau +
                ", roomType=" + roomType +
                ", next=" + next +
                '}';
    }
}
