package com.xiuxiu.app.protocol.client.mahjong;

public class PCLIMahjongNtfGameOverInfoByWHHH extends PCLIMahjongNtfGameOverInfo {
    public static class ScoreInfo extends PCLIMahjongNtfGameOverInfo.ScoreInfo {
        public int extraScore;

        @Override
        public String toString() {
            return "ScoreInfo{" +
                    "extraScore=" + extraScore +
                    ", barCnt=" + barCnt +
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

    public boolean isJFYLF = false;                 // 见风原癞翻倍

    @Override
    public String toString() {
        return "PCLIMahjongNtfGameOverInfoByWHMJ{" +
                ", allPlayer=" + allPlayer +
                ", bureau=" + bureau +
                ", roomType=" + roomType +
                ", next=" + next +
                '}';
    }
}
