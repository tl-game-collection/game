package com.xiuxiu.app.protocol.client.room;

import com.xiuxiu.app.protocol.client.PCLIScoreInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PCLIRoomNtfScoreRecordInfo {
    public static class RoomScoreRecord {
        public long uid;
        public long roomUid;
        public int roomId;
        public int gameType;
        public int gameSubType;
        public long beginTime;
        public long endTime;
        public PCLIScoreInfo totalScore;
        public LinkedList<PCLIScoreInfo> record = new LinkedList<>();
        public HashMap<String, Integer> rule;

        @Override
        public String toString() {
            return "RoomScoreRecord{" +
                    "uid=" + uid +
                    ", roomUid=" + roomUid +
                    ", roomId=" + roomId +
                    ", gameType=" + gameType +
                    ", gameSubType=" + gameSubType +
                    ", beginTime=" + beginTime +
                    ", endTime=" + endTime +
                    ", totalScore=" + totalScore +
                    ", rule=" + rule +
                    ", record=" + record +
                    '}';
        }
    }

    public int page;
    public boolean hasNext;
    public List<RoomScoreRecord> list = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIRoomNtfScoreRecordInfo{" +
                "page=" + page +
                ", hasNext=" + hasNext +
                ", list=" + list +
                '}';
    }
}
