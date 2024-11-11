package com.xiuxiu.app.protocol.api.temp.trade;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.List;

public class SearchTreasurerResp extends ErrorMsg {
    public static class TreasurerPlayerInfo {
        public long clubUid; //圈id
        public String clubName; //圈名称
        public long playerUid;            //上分财务的id
        public String name;         //上分财务的name
        public long score;
        public String desc;         //上分财务描述
        public long registerTime; //财务注册时间

        public TreasurerPlayerInfo() {

        }

        public TreasurerPlayerInfo(long playerUid,String name,long score) {
            this.name = name;
            this.score = score;
            this.playerUid = playerUid;
        }

        @Override
        public String toString() {
            return "TreasurerPlayerInfo{" +
                    "clubName='" + clubName + '\'' +
                    ", name='" + name + '\'' +
                    ", desc='" + desc + '\'' +
                    ",score=" + score +
                    ",playerUid" + playerUid +
                    '}';
        }
    }
    public List<TreasurerPlayerInfo> players;

    @Override
    public String toString() {
        return "SearchTreasurerResp{" +
                "players='" + players + '\'' +
                '}';
    }
}
