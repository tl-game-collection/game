package com.xiuxiu.app.protocol.client.mahjong;

import java.util.HashMap;

public class PCLIMahjongNtfHuInfo {
    public static class HuInfo {
        public int paiXing;
        public int paiXingValue;

        @Override
        public String toString() {
            return "HuInfo{" +
                    "paiXing=" + paiXing +
                    ", paiXingValue=" + paiXingValue +
                    '}';
        }
    }

    public static class ScoreInfo {
        public String totalScore;
        public int paiXingValue;

        @Override
        public String toString() {
            return "ScoreInfo{" +
                    "totalScore='" + totalScore + '\'' +
                    ", paiXingValue=" + paiXingValue +
                    '}';
        }
    }

    public long takePlayerUid = -1;
    public byte huCard = -1;
    public HashMap<Long, HuInfo> allHuInfo = new HashMap<>();
    public HashMap<Long, ScoreInfo> allScoreInfo = new HashMap<>();

    @Override
    public String toString() {
        return "PCLIMahjongNtfHuInfo{" +
                "takePlayerUid=" + takePlayerUid +
                ", huCard=" + huCard +
                ", allHuInfo=" + allHuInfo +
                ", allScoreInfo=" + allScoreInfo +
                '}';
    }
}
