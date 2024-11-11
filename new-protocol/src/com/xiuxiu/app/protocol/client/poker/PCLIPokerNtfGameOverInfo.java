package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomGameOverInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIPokerNtfGameOverInfo extends PCLIRoomGameOverInfo {
    public static class GameOverInfo {
        public List<Byte> card = new ArrayList<>();                 // 剩余手牌
        public String score;                                        // 本局积分
        public String totalScore;                                   // 总积分
        public TotalCnt totalCnt;                                   // 大结算计数
        public boolean isCloseDoor;                                 // 是否关门
        public int cardType;                                        // 牌型  0 表示正常 1表示无花果 2 表示8对
        public String gold;                                   // 竞技分

        @Override
        public String toString() {
            return "GameOverInfo{" +
                    "card=" + card +
                    ", score='" + score + '\'' +
                    ", totalScore='" + totalScore + '\'' +
                    ", totalCnt=" + totalCnt +
                    ", cardType=" + cardType +
                    ", isCloseDoor=" + isCloseDoor +
                    ", gold=" + gold +
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

    public HashMap<Long, GameOverInfo> allGameOverInfo = new HashMap<>();

    @Override
    public String toString() {
        return "PCLIPokerNtfGameOverInfo{" +
                "allGameOverInfo=" + allGameOverInfo +
                ", bureau=" + bureau +
                ", roomType=" + roomType +
                ", next=" + next +
                '}';
    }
}
