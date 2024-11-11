package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

public class GetGroupListResp extends ErrorMsg {
    public static class GroupInfo {
        public long gid;
        public String groupName;
        public long groupOwner;
        public String groupOwnerName;
        public long totalServiceValue;
        public long totalArenaValue;
        public long totalCostDiamond;
        public long totalIncArenaValueByWallet;
        public long totalDecArenaValueByWallet;

        @Override
        public String toString() {
            return "GroupInfo{" +
                    "gid=" + gid +
                    ", groupName='" + groupName + '\'' +
                    ", groupOwner=" + groupOwner +
                    ", groupOwnerName='" + groupOwnerName + '\'' +
                    ", totalServiceValue=" + totalServiceValue +
                    ", totalArenaValue=" + totalArenaValue +
                    ", totalCostDiamond=" + totalCostDiamond +
                    ", totalIncArenaValueByWallet=" + totalIncArenaValueByWallet +
                    ", totalDecArenaValueByWallet=" + totalDecArenaValueByWallet +
                    '}';
        }
    }
    public static class Data {
        public List<GroupInfo> list = new ArrayList<>();    // 群列表
        public long count;                                  // 群总数量

        @Override
        public String toString() {
            return "Data{" +
                    "list=" + list +
                    ", count=" + count +
                    '}';
        }
    }
    public Data data = new Data();

    @Override
    public String toString() {
        return "GetGroupListResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
