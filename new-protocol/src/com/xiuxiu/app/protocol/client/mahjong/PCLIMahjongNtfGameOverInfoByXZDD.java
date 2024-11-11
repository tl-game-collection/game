package com.xiuxiu.app.protocol.client.mahjong;

public class PCLIMahjongNtfGameOverInfoByXZDD extends PCLIMahjongNtfGameOverInfo {
    public static class ScoreInfo extends PCLIMahjongNtfGameOverInfo.ScoreInfo {
        @Override
        public String toString() {
            return "ScoreInfo{" +
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
        public int huaZhuValue;             // 查大叫分数
        public int daJiaoValue;             // 查花猪分数


        @Override
        public String toString() {
            return "PlayerInfo{" +
                    "isChaHuaZhu=" + isChaHuaZhu +
                    ", isChaDaJiao=" + isChaDaJiao +
                    ", huaZhuValue=" + huaZhuValue +
                    ", daJiaoValue=" + daJiaoValue +
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
        return "PCLIMahjongNtfGameOverInfoByXZDD{" +
                "allPlayer=" + allPlayer +
                ", bureau=" + bureau +
                ", roomType=" + roomType +
                ", next=" + next +
                '}';
    }
}
