package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MoneyExpendRecordListResp extends ErrorMsg {
    public static class MoneyExpendRecord{
        public String expendTime;
        public int roomType;
        public int count;

        @Override
        public String toString() {
            return "MoneyExpendRecord{" +
                    "expendTime=" + expendTime +
                    ", roomType=" + roomType +
                    ", count=" + count +
                    '}';
        }
    }

    public int page;
    public int pageSize;
    public List<MoneyExpendRecord> info = new ArrayList<>();

    @Override
    public String toString() {
        return "MoneyExpendRecordListResp{" +
                "page=" + page +
                ", pageSize=" + pageSize +
                ", info=" + info +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
