package com.xiuxiu.app.protocol.client.player;

import java.util.ArrayList;
import java.util.List;

public class PCLIPlayerNtfWalletRecord {
    public static class RecordList {
        private long time;                 // 月份时间戳
        private long inMoney;                // 当月总收入
        private long outMoney;               // 当月总支出

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public long getInMoney() {
            return inMoney;
        }

        public void setInMoney(long inMoney) {
            this.inMoney = inMoney;
        }

        public long getOutMoney() {
            return outMoney;
        }

        public void setOutMoney(long outMoney) {
            this.outMoney = outMoney;
        }

        @Override
        public String toString() {
            return "RecordList{" +
                    "time='" + time + '\'' +
                    ", inMoney=" + inMoney +
                    ", outMoney=" + outMoney +
                    '}';
        }
    }

    public List<RecordList> recordList = new ArrayList<>();      // 记录列表

    @Override
    public String toString() {
        return "PCLIPlayerNtfWalletRecord{" +
                "recordList=" + recordList +
                '}';
    }
}
