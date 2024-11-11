package com.xiuxiu.app.protocol.api.temp.account;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.List;

public class StatAccountActionsResp extends ErrorMsg {
    public Data data;

    public StatAccountActionsResp(ErrorCode err) {
        super(err);
    }

    @Override
    public String toString() {
        return "StatAccountActionsResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }

    public static class Data {
        public String type;
        public long timeBegin;
        public long timeEnd;
        public int period;
        public List<Long> list;

        public Data(String type, long timeBegin, long timeEnd, int period) {
            this.type = type;
            this.timeBegin = timeBegin;
            this.timeEnd = timeEnd;
            this.period = period;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "type='" + type + '\'' +
                    ", timeBegin=" + timeBegin +
                    ", timeEnd=" + timeEnd +
                    ", period=" + period +
                    ", list=" + list +
                    '}';
        }
    }
}
