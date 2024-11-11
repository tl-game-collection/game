package com.xiuxiu.app.protocol.api.league;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

public class LeagueGetListResp extends ErrorMsg {
    public static class LeagueInfo {
        public long uid;
        public String name;
        public String desc;
        public String gameDesc;
        public long leaderUid;
        public String leaderName;
        public int groupSize;
        public int num;
        public int costDiamond;
        public long createTime;
        public int openJoin;//0不可见，1可见


        @Override
        public String toString() {
            return "LeagueInfo{" +
                    "uid=" + uid +
                    ", name='" + name + '\'' +
                    ", desc='" + desc + '\'' +
                    ", gameDesc='" + gameDesc + '\'' +
                    ", leaderUid=" + leaderUid +
                    ", leaderName='" + leaderName + '\'' +
                    ", groupSize=" + groupSize +
                    ", num=" + num +
                    ", costDiamond=" + costDiamond +
                    ", createTime=" + createTime +
                    ", openJoin=" + openJoin +
                    '}';
        }
    }

    public int page;
    public int pageSize;
    public int count;
    public List<LeagueInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        return "LeagueGetListResp{" +
                "page=" + page +
                ", pageSize=" + pageSize +
                ", count=" + count +
                ", list=" + list +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
