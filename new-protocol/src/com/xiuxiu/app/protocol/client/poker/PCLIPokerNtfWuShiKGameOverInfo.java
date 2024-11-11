package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomGameOverInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIPokerNtfWuShiKGameOverInfo extends PCLIRoomGameOverInfo {
    public HashMap<Long, GameOverInfo> allGameOverInfo = new HashMap<>();
    public int overType; // 2-双抓，1-单抓，0-保
    public List<Long> ranking = new ArrayList<>();              // 名次，依序排列

    public static class GameOverInfo {
        public String name;                                         // 昵称
        public String icon;                                         // 头像
        public List<Byte> card = new ArrayList<>();                 // 剩余手牌
        public String score;                                        // 本局积分
        public String totalScore;                                   // 总积分
        public TotalCnt totalCnt;                                   // 大结算计数
        public boolean isCloseDoor;                                 // 是否关门
        public int wskScore;                                        // 本局获得的五十K分
        public int boomScore;                                       // 本局获得的炸弹分
        public int talScore;                                        // 总得分
        public long partnerUID;                                     // 盟友Uid

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
                    ", wskScore=" + wskScore +
                    ", boomScore=" + boomScore +
                    ", talScore=" + talScore +
                    ", partnerUID=" + partnerUID +
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
        return "PCLIPokerNtfWuShiKGameOverInfo{" +
                "allGameOverInfo=" + allGameOverInfo +
                ", overType=" + overType +
                ", bureau=" + bureau +
                ", roomType=" + roomType +
                ", next=" + next +
                ", ranking=" + ranking +
                '}';
    }
}
