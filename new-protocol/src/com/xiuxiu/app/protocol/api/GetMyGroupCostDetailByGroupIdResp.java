package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.List;

public class GetMyGroupCostDetailByGroupIdResp extends ErrorMsg {
    public static class GroupCostDiamondDetailInfo {
        public long cost;
        public long time;

        @Override
        public String toString() {
            return "CostSum{" +
                    "cost=" + cost +
                    ", time=" + time +
                    '}';
        }

    }

    public Data data;

    public static class Data{
        public long groupId;
        public String groupName;
        public String groupIcon;
        public List<GroupCostDiamondDetailInfo> list;
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
