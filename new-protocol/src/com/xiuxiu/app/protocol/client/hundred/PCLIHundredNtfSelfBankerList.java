package com.xiuxiu.app.protocol.client.hundred;

import java.util.ArrayList;

public class PCLIHundredNtfSelfBankerList {
    public static class SelfBankerInfo{
        public long bankerUid;
        public long playerUid;
        public String playerName;
        public String playerIcon;
        public String value;
        public int num;//序号

        @Override
        public String toString() {
            return "SelfBankerInfo{" +
                    "bankerUid=" + bankerUid +
                    ", playerUid=" + playerUid +
                    ", playerName='" + playerName + '\'' +
                    ", playerIcon='" + playerIcon + '\'' +
                    ", value='" + value + '\'' +
                    ", num = " + num +
                    '}';
        }
    }

    public long boxId;
    public long roomId;
    public int page;
    public int pageSize;
    public boolean next;
    public ArrayList<SelfBankerInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIHundredNtfSelfBankerList{" +
                "boxId=" + boxId +
                ", roomId=" + roomId +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", next=" + next +
                ", list=" + list +
                '}';
    }
}
