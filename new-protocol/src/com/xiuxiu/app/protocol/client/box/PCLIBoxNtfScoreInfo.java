package com.xiuxiu.app.protocol.client.box;

import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfScoreRecordInfo;

import java.util.ArrayList;
import java.util.List;

public class PCLIBoxNtfScoreInfo {

    public static class BoxRoomScoreRecord extends PCLIRoomNtfScoreRecordInfo.RoomScoreRecord {
        public long groupUid;
        public long boxUid;
        public int roomType;//房间类型 1.可少人 2.2人场 3.3人场

        @Override
        public String toString() {
            return "BoxRoomScoreRecord{" +
                    "groupUid=" + groupUid +
                    ", boxUid=" + boxUid +
                    ", uid=" + uid +
                    ", roomUid=" + roomUid +
                    ", roomId=" + roomId +
                    ", gameType=" + gameType +
                    ", gameSubType=" + gameSubType +
                    ", roomType=" + roomType +
                    ", beginTime=" + beginTime +
                    ", endTime=" + endTime +
                    ", totalScore=" + totalScore +
                    ", record=" + record +
                    '}';
        }
    }

    public long playerUid;          //查看的玩家uid
    public boolean next;
    public int page;
    public List<BoxRoomScoreRecord> list = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIBoxNtfScoreInfo{" +
                "playerUid=" + playerUid +
                ", next=" + next +
                ", page=" + page +
                ", list=" + list +
                '}';
    }
}

