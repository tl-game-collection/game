package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomGameOverInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIPokerNtfWskGameOverInfo extends PCLIRoomGameOverInfo {
    public static class GameOverInfo {
        public List<Byte> card = new ArrayList();
        public String score;
        public String totalScore;
        public PCLIPokerNtfWskGameOverInfo.TotalCnt totalCnt;
        public long frindUid;

        @Override
        public String toString() {
            return "GameOverInfo{" +
                    "card=" + card +
                    ", score='" + score + '\'' +
                    ", totalScore='" + totalScore + '\'' +
                    ", totalCnt=" + totalCnt +
                    ", frindUid=" + frindUid +
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

    public HashMap<Long, PCLIPokerNtfWskGameOverInfo.GameOverInfo> allGameOverInfo = new HashMap<>();

    @Override
    public String toString() {
        return "PCLIPokerNtfWskGameOverInfo{" +
                "allGameOverInfo=" + allGameOverInfo +
                ", bureau=" + bureau +
                ", roomType=" + roomType +
                ", next=" + next +
                '}';
    }

}


