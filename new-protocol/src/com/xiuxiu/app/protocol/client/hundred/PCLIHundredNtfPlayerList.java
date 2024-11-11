package com.xiuxiu.app.protocol.client.hundred;

import java.util.ArrayList;

public class PCLIHundredNtfPlayerList {
    public static class PlayerInfo {
        public long playerUid;
        public String playerName;
        public String playerIcon;
        public String value;

        @Override
        public String toString() {
            return "PlayerInfo{" +
                    "playerUid=" + playerUid +
                    ", playerName='" + playerName + '\'' +
                    ", playerIcon='" + playerIcon + '\'' +
                    ", value=" + value +
                    '}';
        }
    }

    public long boxId;
    public long roomId;
    public int page;
    public boolean next;
    public ArrayList<PlayerInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIHundredNtfPlayerList{" +
                "boxId=" + boxId +
                ", roomId=" + roomId +
                ", page=" + page +
                ", next=" + next +
                ", list=" + list +
                '}';
    }
}