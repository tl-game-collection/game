package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomGameOverInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIPokerNtfArchGameOverInfo extends PCLIRoomGameOverInfo {
    public HashMap<Long, GameOverInfo> allGameOverInfo = new HashMap<>();
    public int overType; // 1-半拱，2-直拱，4-干带直

    public static class GameOverInfo {
        public String name;                                         // 昵称
        public String icon;                                         // 头像
        public List<Byte> card = new ArrayList<>();                 // 剩余手牌
        public String score;                                        // 本局积分
        public String totalScore;                                   // 总积分
        public TotalCnt totalCnt;                                   // 大结算计数
        public boolean isCloseDoor;                                 // 是否关门
        public int cardScore;                                       // 本局获得的牌分
        public int huaCnt;                                          // 花牌数量
        public List<ScorePoint> scorePoints = new ArrayList<>();    // 得分点

        @Override
        public String toString() {
            return "GameOverInfo{" +
                    "name='" + name + '\'' +
                    ", icon='" + icon + '\'' +
                    ", card=" + card +
                    ", score='" + score + '\'' +
                    ", totalScore='" + totalScore + '\'' +
                    ", totalCnt=" + totalCnt +
                    ", isCloseDoor=" + isCloseDoor +
                    ", cardScore=" + cardScore +
                    ", huaCnt=" + huaCnt +
                    ", scorePoints=" + scorePoints +
                    '}';
        }
    }

    public static class TotalCnt {
        public int winCnt;
        public int lostCnt;
        public int bombCnt;

        @Override
        public String toString() {
            return "TotalCnt{" +
                    "winCnt=" + winCnt +
                    ", lostCnt=" + lostCnt +
                    ", bombCnt=" + bombCnt +
                    '}';
        }
    }

    public static class ScorePoint {
        public int key;
        public int value;

        public ScorePoint(int key, int value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "key=" + key +
                    ", value=" + value +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PCLIPokerNtfArchGameOverInfo{" +
                "allGameOverInfo=" + allGameOverInfo +
                ", overType=" + overType +
                ", bureau=" + bureau +
                ", roomType=" + roomType +
                ", next=" + next +
                '}';
    }
}
