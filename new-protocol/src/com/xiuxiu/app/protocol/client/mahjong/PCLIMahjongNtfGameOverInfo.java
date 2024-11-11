package com.xiuxiu.app.protocol.client.mahjong;

import com.xiuxiu.app.protocol.client.room.PCLIRoomGameOverInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIMahjongNtfGameOverInfo extends PCLIRoomGameOverInfo {
    public static class ScoreInfo {
        public int barCnt;              // 杠次数
        public int gangScore;           // 杠分
        public int fangScore;           // 牌型分
        public int huScore;             // 胡分
        public String score;            // 总分
        public String totalScore;       // 目前为止总分

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

    public static class FinalResult {
        public int ziMoCnt = 0;                                                     // 自摸次数
        public int anGangCnt = 0;                                                   // 暗杠次数
        public int mingGangCnt = 0;                                                 // 明杠次数
        public int huCnt = 0;                                                       // 胡次数
        public int fangPaoCnt = 0;                                                  // 放炮次数
        public int score = 0;                                                       // 总分

        @Override
        public String toString() {
            return "FinalResult{" +
                    "ziMoCnt=" + ziMoCnt +
                    ", anGangCnt=" + anGangCnt +
                    ", mingGangCnt=" + mingGangCnt +
                    ", huCnt=" + huCnt +
                    ", fangPaoCnt=" + fangPaoCnt +
                    ", score=" + score +
                    '}';
        }
    }

    public static class HuInfo {
        public boolean ziMo;            // 是否自摸
        public byte huCard;             // 胡牌
        public int paiXing;             // 牌型
        public int fang;                // 番数
        public long takePlayerUid;      // 打牌玩家uid

        @Override
        public String toString() {
            return "HuInfo{" +
                    "ziMo=" + ziMo +
                    ", huCard=" + huCard +
                    ", paiXing=" + paiXing +
                    ", fang=" + fang +
                    ", takePlayerUid=" + takePlayerUid +
                    '}';
        }
    }

    public static class PlayerInfo {
        public ScoreInfo score = null;                              // 分数
        public FinalResult finalResult = null;                      // 最终结果
        public List<Byte> handCard = new ArrayList<>();             // 手牌
        public List<HuInfo> allHuInfo = new ArrayList<>();          // 所有胡信息
        public HashMap<String, Integer> allShow = new HashMap<>();  // 所有显示牌型

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

    public HashMap<Long, PlayerInfo> allPlayer = new HashMap<>();   // 所有玩家信息

    @Override
    public String toString() {
        return "PCLIMahjongNtfGameOverInfo{" +
                "allPlayer=" + allPlayer +
                ", bureau=" + bureau +
                ", roomType=" + roomType +
                ", next=" + next +
                '}';
    }
}
