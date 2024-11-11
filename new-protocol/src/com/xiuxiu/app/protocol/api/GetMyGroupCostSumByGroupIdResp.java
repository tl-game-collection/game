package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.List;

public class GetMyGroupCostSumByGroupIdResp extends ErrorMsg {
    public static class GroupCostDiamondSumInfo {
        public long uid;
        public String name;
        public String icon = "";
        public int costSum = 0;

        @Override
        public String toString() {
            return "CostSum{" +
                    "uid=" + uid +
                    ", name='" + uid + '\'' +
                    ", icon='" + icon + '\'' +
                    ", costSum=" + costSum +
                    '}';
        }

    }

    public Data data;

    public static class Data{
        public List<GroupCostDiamondSumInfo> list;
    }

    @Override
    public String toString() {
        return "GetMyGroupCostSumByGroupIdResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
