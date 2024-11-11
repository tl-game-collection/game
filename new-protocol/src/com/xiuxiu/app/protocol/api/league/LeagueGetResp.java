package com.xiuxiu.app.protocol.api.league;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

public class LeagueGetResp extends ErrorMsg {
    public static class LeagueItemInfo {
        public long uid;
        public String name;
        public long leaderUid;
        public String leaderString;
        public int num;
        public long costDiamond;
        public long joinTime;

        @Override
        public String toString() {
            return "LeagueItemInfo{" +
                    "uid=" + uid +
                    ", name='" + name + '\'' +
                    ", leaderUid=" + leaderUid +
                    ", leaderString='" + leaderString + '\'' +
                    ", num=" + num +
                    ", costDiamond=" + costDiamond +
                    ", joinTime=" + joinTime +
                    '}';
        }
    }

    public long uid;
    public int page;
    public int pageSize;
    public int count;
    public List<LeagueItemInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        return "LeagueGetResp{" +
                "uid=" + uid +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", count=" + count +
                ", list=" + list +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
