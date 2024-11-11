package com.xiuxiu.app.protocol.client.hundred;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIHundredNtfDeskInfoByLhd extends PCLIHundredNtfDeskInfo {
    public static class RebInfo {
        public HashMap<Integer, Integer> allReb = new HashMap<>();
        public HashMap<Integer, Integer> myReb = new HashMap<>();
        public List<Byte> card = new ArrayList<>();
        public int cardType;
        public boolean win;

        @Override
        public String toString() {
            return "RebInfo{" +
                    "allReb=" + allReb +
                    ", myReb=" + myReb +
                    ", card=" + card +
                    ", cardType=" + cardType +
                    ", win=" + win +
                    '}';
        }
    }

    public List<RebInfo> allReb = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIHundredNtfDeskInfoByLhd{" +
                "allReb=" + allReb +
                ", boxId=" + boxId +
                ", roomId=" + roomId +
                ", groupUid=" + groupUid +
                ", gameType=" + gameType +
                ", bankerPlayerUid=" + bankerPlayerUid +
                ", bankerPlayerName='" + bankerPlayerName + '\'' +
                ", bankerPlayerIcon='" + bankerPlayerIcon + '\'' +
                ", bankerUid=" + bankerUid +
                ", bankerValue='" + bankerValue + '\'' +
                ", remainRebValue=" + remainRebValue +
                ", curBureau=" + curBureau +
                ", rule=" + rule +
                ", state=" + state +
                ", remainTime=" + remainTime +
                ", ownerType=" + ownerType +
                ", vipSeatInfoList=" + vipSeatInfoList +
                '}';
    }
}
