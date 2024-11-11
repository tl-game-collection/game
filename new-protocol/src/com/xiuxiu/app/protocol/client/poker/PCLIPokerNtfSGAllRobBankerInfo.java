package com.xiuxiu.app.protocol.client.poker;

import java.util.HashMap;

public class PCLIPokerNtfSGAllRobBankerInfo {
    public HashMap<Long/*playerUid*/, Integer/*multiple, -1: 表示还没有抢*/> allInfo = new HashMap<>();

    @Override
    public String toString() {
        return "PCLIPokerNtfSGAllRobBankerInfo{" +
                "allInfo=" + allInfo +
                '}';
    }
}
