package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

public class GetDailyActiveCountResp extends ErrorMsg {
    public static class ActiveCount{
        public String date;
        public int activeNum;

        public ActiveCount() {

        }

        @Override
        public String toString() {
            return "ActiveCount{" +
                    "date='" + date + '\'' +
                    ", activeNum=" + activeNum +
                    '}';
        }
    }

    public List<ActiveCount> data = new ArrayList<>();

    public GetDailyActiveCountResp() {

    }

    @Override
    public String toString() {
        return "GetDailyActiveCountResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
