package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

public class GetDailyServiceChargeResp extends ErrorMsg {
    public static class DailyServiceCharge{
        public long date;
        public int cost;

        public DailyServiceCharge() {

        }

        @Override
        public String toString() {
            return "DailyServiceCharge{" +
                    "date=" + date +
                    ", cost=" + cost +
                    '}';
        }
    }

    public List<DailyServiceCharge> data = new ArrayList<>();

    @Override
    public String toString() {
        return "GetDailyServiceChargeResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
