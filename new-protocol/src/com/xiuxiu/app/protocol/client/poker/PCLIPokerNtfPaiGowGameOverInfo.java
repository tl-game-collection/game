package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomGameOverInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIPokerNtfPaiGowGameOverInfo extends PCLIRoomGameOverInfo {
    public static class GameOverInfo {
        public List<Byte> card = new ArrayList<>();                 // 剩余手牌
        public String score;                                        // 本局积分
        public String totalScore;                                   // 总积分
        public TotalCnt totalCnt;                                   // 大结算计数
        public boolean isCloseDoor;                                 // 是否关门
        public int bureau;

        @Override
        public String toString() {
            return "GameOverInfo{" +
                    "card=" + card +
                    ", score='" + score + '\'' +
                    ", totalScore='" + totalScore + '\'' +
                    ", totalCnt=" + totalCnt +
                    ", isCloseDoor=" + isCloseDoor +
                    ", bureau=" + bureau +
                    '}';
        }
    }

    public static class TotalCnt {
        public int maxCardType;         // 最大牌型
        public int maxScore;            // 当局最高赢的分数
        public int winCnt;
        public int lostCnt;
        public int bombCnt;

        @Override
        public String toString() {
            return "TotalCnt{" +
                    "maxCardType=" + maxCardType +
                    ", maxScore=" + maxScore +
                    ", winCnt=" + winCnt +
                    ", lostCnt=" + lostCnt +
                    ", bombCnt=" + bombCnt +
                    '}';
        }
    }

    public HashMap<Long, GameOverInfo> allGameOverInfo = new HashMap<>();
    public int curHotDeskNote;
    public int bankerBureau; // 加锅牌九，连庄局数
    public int keepHotCount; // 已续锅次数

    @Override
    public String toString() {
        return "PCLIPokerNtfPaiGowGameOverInfo{" +
                "allGameOverInfo=" + allGameOverInfo +
                ", curHotDeskNote=" + curHotDeskNote +
                ", bankerBureau=" + bankerBureau +
                ", keepHotCount=" + keepHotCount +
                ", bureau=" + bureau +
                ", roomType=" + roomType +
                ", next=" + next +
                '}';
    }
}
