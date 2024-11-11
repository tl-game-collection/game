package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class GetMyGroupCostSumResp extends ErrorMsg {
    public static class CostSum {
        public long sumCost = 0;
        public String sign;             // md5(sumCost + key)

        @Override
        public String toString() {
            return "CostSum{" +
                    "sumCost=" + sumCost +
                    ", sign='" + sign + '\'' +
                    '}';
        }

    }

    public CostSum data;

    @Override
    public String toString() {
        return "GetMyGroupCostSumResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
