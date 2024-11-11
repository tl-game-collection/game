package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

public class GetWalletRecordByPlayerResp extends ErrorMsg {
    public static class WalletRecord {
        public long playerUid;
        public int amount;
        public String desc;
        public String time;

        @Override
        public String toString() {
            return "WalletRecord{" +
                    "playerUid=" + playerUid +
                    ", amount=" + amount +
                    ", desc='" + desc + '\'' +
                    ", time=" + time +
                    '}';
        }
    }
    public static class Data{
        public List<WalletRecord> list = new ArrayList<>();
        public long count;

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
        return "GetWalletRecordByPlayerResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
