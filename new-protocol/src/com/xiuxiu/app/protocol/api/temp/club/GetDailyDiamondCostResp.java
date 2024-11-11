package com.xiuxiu.app.protocol.api.temp.club;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

public class GetDailyDiamondCostResp extends ErrorMsg {
    public static class DailyDiamondCost{
        public long date;
        public int cost;

        public DailyDiamondCost() {

        }

        @Override
        public String toString() {
            return "DailyDiamondCost{" +
                    "date='" + date + '\'' +
                    ", cost=" + cost +
                    '}';
        }
    }

    public List<DailyDiamondCost> data = new ArrayList<>();

    public GetDailyDiamondCostResp() {

    }

    @Override
    public String toString() {
        return "GetDailyDiamondCostResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
