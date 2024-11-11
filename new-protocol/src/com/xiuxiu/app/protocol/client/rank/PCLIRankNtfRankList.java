package com.xiuxiu.app.protocol.client.rank;

import java.util.ArrayList;
import java.util.List;

public class PCLIRankNtfRankList {
    public static class RankInfo {
        public long playerUid;
        public long lastTime;    // 时间
        public int value;
        public String name;

        @Override
        public String toString() {
            return "RankInfo{" +
                    ", playerUid=" + playerUid +
                    ", value= " + value  +
                    ", lastTime= " + lastTime  +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public int page;
    public boolean hasNext;
    public String sValue;
    public List<RankInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIRankNtfRankList{" +
                "page=" + page +
                ", hasNext=" + hasNext +
                ", sValue=" + sValue + '\'' +
                ", list=" + list +
                '}';
    }
}
