package com.xiuxiu.app.protocol.client.hundred;

import java.util.ArrayList;

public class PCLIHundredNtfBankerList {
    public static class BankerInfo {
        public long bankerUid;
        public long playerUid;
        public String playerName;
        public String playerIcon;
        public String value;

        @Override
        public String toString() {
            return "BankerInfo{" +
                    "bankerUid=" + bankerUid +
                    ", playerUid=" + playerUid +
                    ", playerName='" + playerName + '\'' +
                    ", playerIcon='" + playerIcon + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public long boxId;
    public long roomId;
    public int page;
    public boolean next;
    public ArrayList<BankerInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIHundredNtfBankerList{" +
                "boxId=" + boxId +
                ", roomId=" + roomId +
                ", page=" + page +
                ", next=" + next +
                ", list=" + list +
                '}';
    }
}
