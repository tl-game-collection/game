package com.xiuxiu.app.protocol.client.mahjong;

public class PCLIMahjongNtfGameOverInfoByYCXL extends PCLIMahjongNtfGameOverInfo {
    public static class ScoreInfo extends PCLIMahjongNtfGameOverInfo.ScoreInfo {
        public int piaoScore;           // 飘分

        @Override
        public String toString() {
            return "ScoreInfo{" +
                    "piaoScore=" + piaoScore +
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
        public boolean isChaHuaZhu;         // 是否查花猪
        public boolean isChaDaJiao;         // 是否查大叫
        public int chaValue;                // 查大叫/查花猪分数
        public boolean ting;

        @Override
        public String toString() {
            return "PlayerInfo{" +
                    "isChaHuaZhu=" + isChaHuaZhu +
                    ", isChaDaJiao=" + isChaDaJiao +
                    ", chaValue=" + chaValue +
                    ", ting=" + ting +
                    ", score=" + score +
                    ", finalResult=" + finalResult +
                    ", handCard=" + handCard +
                    ", allHuInfo=" + allHuInfo +
                    ", allShow=" + allShow +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "PCLIMahjongNtfGameOverInfoByYCXL{" +
                ", allPlayer=" + allPlayer +
                ", bureau=" + bureau +
                ", roomType=" + roomType +
                ", next=" + next +
                '}';
    }
}
