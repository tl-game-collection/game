package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

public class GetLogAccountRemainResp extends ErrorMsg {
    public static class LogAccountRemain{
        public long date;
        public long registerNum;
        public int day_2;
        public int day_3;
        public int day_4;
        public int day_5;
        public int day_6;
        public int day_7;
        public int day_14;
        public int day_30;

        public LogAccountRemain() {
        }

        @Override
        public String toString() {
            return "LogAccountRemain{" +
                    "date=" + date +
                    ", registerNum=" + registerNum +
                    ", day_2=" + day_2 +
                    ", day_3=" + day_3 +
                    ", day_4=" + day_4 +
                    ", day_5=" + day_5 +
                    ", day_6=" + day_6 +
                    ", day_7=" + day_7 +
                    ", day_14=" + day_14 +
                    ", day_30=" + day_30 +
                    '}';
        }
    }

    public List<LogAccountRemain> data = new ArrayList<>();

    public GetLogAccountRemainResp() {

    }

    @Override
    public String toString() {
        return "GetLogAccountRemainResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
