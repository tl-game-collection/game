package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.List;

public class PCLIMahjongNtfGameOverInfoByCSMJ extends PCLIMahjongNtfGameOverInfo {
    public static class ScoreInfo extends PCLIMahjongNtfGameOverInfo.ScoreInfo {
        public int niaoScore;           // 鸟分
        public int zengScore;           // 飘分
        public int startHuScore;        // 起手胡分数

        @Override
        public String toString() {
            return "ScoreInfo{" +
                    "niaoScore=" + niaoScore +
                    ", zengScore=" + zengScore +
                    ", startHuScore=" + startHuScore +
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
        return "PCLIMahjongNtfGameOverInfoByCSMJ{" +
                "niaoList=" + niaoList +
                ", allPlayer=" + allPlayer +
                ", bureau=" + bureau +
                ", roomType=" + roomType +
                ", next=" + next +
                '}';
    }
}
