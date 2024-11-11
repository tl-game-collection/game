package com.xiuxiu.app.protocol.client.hundred;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PCLIHundredNtfBankerRecord {
    public static class ArenaBankerRebInfo {
        public int value;//下注值
        public int cardType;//牌型
        public List<Byte> cards;//牌值 没有为null
        public int winValue;//赢值（输了为负数）
        @Override
        public String toString() {
            return "ArenaBankerRebInfo{" +
                    "value=" + value +
                    ", cardType=" + cardType +
                    ", cards=" + cards +
                    ", winValue=" + winValue +
                    '}';
        }
    }
    public static class ArenaBankerRecordInfo {
        //所有下注区的下注值
        public Map<Integer,ArenaBankerRebInfo> rebs = new HashMap<>();
        public ArenaBankerRebInfo bankerReb;//庄家的押注输赢 没有则为null
        public int value;//输赢值
        public long time;//时间
        //  public int bureau;//第几局
        @Override
        public String toString() {
            return "PCLIHundredArenaNtfBankerRecord{" +
                    ",bankerReb=" + bankerReb +
                    ",rebs=" + rebs +
                    ", value=" + value +
                    ", time=" + time +
                    // ", bureau=" + bureau +
                    '}';
        }
    }

    public long boxId;
    public long roomId;
    public int page;
    public boolean next;
    //记录
    public List<ArenaBankerRecordInfo> records = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIHundredNtfBankerRecord{" +
                "boxId=" + boxId +
                ", roomId=" + roomId +
                ", page=" + page +
                ", next=" + next +
                ", records=" + records +
                '}';
    }
}
