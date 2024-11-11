package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.List;

public class PCLIMahjongNtfGameOverInfoByZZMJ extends PCLIMahjongNtfGameOverInfo {
    public static class ScoreInfo extends PCLIMahjongNtfGameOverInfo.ScoreInfo {
        public int niaoScore;           // 鸟分
        public int wypn;                // 围一飘鸟
        public int piao;                // 飘分

        @Override
        public String toString() {
            return "ScoreInfo{" +
                    "niaoScore=" + niaoScore +
                    ", wypn=" + wypn +
                    ", piao=" + piao +
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
                    ", score=" + score +
                    ", finalResult=" + finalResult +
                    ", handCard=" + handCard +
                    ", allHuInfo=" + allHuInfo +
                    ", allShow=" + allShow +
                    '}';
        }
    }

    public List<Byte> niaoList = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIMahjongNtfGameOverInfoByZZMJ{" +
                "niaoList=" + niaoList +
                ", allPlayer=" + allPlayer +
                ", bureau=" + bureau +
                ", roomType=" + roomType +
                ", next=" + next +
                '}';
    }
}
