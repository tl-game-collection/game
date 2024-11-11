package com.xiuxiu.app.protocol.client.hundred;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIHundredNtfRecord {
    public static class RebInfo {
        public String rebValue;
        public String winValue;

        @Override
        public String toString() {
            return "RebInfo{" +
                    "rebValue=" + rebValue +
                    ", winValue=" + winValue +
                    '}';
        }
    }

    public static class AllRebInfo {
        public HashMap<Integer, RebInfo> allReb = new HashMap<>();
        public int cardType;
        public List<Byte> cards = new ArrayList<>();

        @Override
        public String toString() {
            return "AllRebInfo{" +
                    "allReb=" + allReb +
                    ", cardType=" + cardType +
                    ", cards=" + cards +
                    '}';
        }
    }

    public static class RebRecord {
        public List<AllRebInfo> rebInfo = new ArrayList<>();
        public int bankerCardType;
        public long time;

        @Override
        public String toString() {
            return "RebRecord{" +
                    "rebInfo=" + rebInfo +
                    ", bankerCardType=" + bankerCardType +
                    ", time=" + time +
                    '}';
        }
    }

    public static class CardInfo {
        public int cardType;
        public boolean win;
        public List<Byte> cards = new ArrayList<>();

        @Override
        public String toString() {
            return "CardInfo{" +
                    "cardType=" + cardType +
                    ", win=" + win +
                    ", cards=" + cards +
                    '}';
        }
    }

    public static class BankerRecord {
        public List<CardInfo> cardInfo = new ArrayList<>();
        public long time;

        @Override
        public String toString() {
            return "BankerRecord{" +
                    "cardInfo=" + cardInfo +
                    ", time=" + time +
                    '}';
        }
    }

    public long boxId;
    public long roomId;
    public int page;
    public int pageSize;
    public boolean next;
    public boolean reb;         // 是否下注, true: 下注, false:局
    public String totalValue;      // 总数
    public ArrayList<RebRecord> rebList = new ArrayList<>();
    public ArrayList<BankerRecord> bankerList = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIHundredNtfRecord{" +
                "boxId=" + boxId +
                ", roomId=" + roomId +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", next=" + next +
                ", reb=" + reb +
                ", totalValue='" + totalValue + '\'' +
                ", rebList=" + rebList +
                ", bankerList=" + bankerList +
                '}';
    }
}
