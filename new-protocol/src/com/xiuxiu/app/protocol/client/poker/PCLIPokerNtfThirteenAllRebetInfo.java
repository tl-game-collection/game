package com.xiuxiu.app.protocol.client.poker;

import java.util.HashMap;

public class PCLIPokerNtfThirteenAllRebetInfo {
    public HashMap<Long, Integer> allInfo = new HashMap();
    public int baseRebet = 0;
    public int pushNote = 0;

    @Override
    public String toString() {
        return "PCLIPokerNtfThirteenAllRebetInfo{" +
                "allInfo=" + allInfo +
                ", baseRebet=" + baseRebet +
                ", pushNote=" + pushNote +
                '}';
    }
}
