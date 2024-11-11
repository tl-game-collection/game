package com.xiuxiu.app.protocol.client.club;

import java.util.ArrayList;
import java.util.List;

public class PCLIClubNtfWarSituationInfo {
    public static class RecordInfo {
        public long uid;   //数据uid
        public long roomId;
        public long clubUid;
        public long playerUid;
        public String playerName;
        public int score;
        public int gameType;
        public int gameSubType;
        public long time;
        public int markstate; //是否标记 0.未标记 1.已标记
    }

    public long clubUid;
    public int page;
    public int pageSize;
    public int yesterdayUse;//昨日消耗
    public int todayUse;    //今日消耗
    public List<RecordInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIClubNtfWarSituationInfo{" +
                "clubUid=" + clubUid +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", yesterdayUse=" + yesterdayUse +
                ", todayUse=" + todayUse +
                ", list=" + list +
                '}';
    }
}
