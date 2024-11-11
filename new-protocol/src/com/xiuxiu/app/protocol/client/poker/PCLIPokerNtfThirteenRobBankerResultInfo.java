package com.xiuxiu.app.protocol.client.poker;

import java.util.HashMap;

public class PCLIPokerNtfThirteenRobBankerResultInfo {
    public HashMap<Long/*playerUid*/, Integer/*multiple*/> allRobBankerInfo = new HashMap<>();      // 抢庄
    public long bankerPlayerUid;        // 庄家玩家uid

    @Override
    public String toString() {
        return "PCLIPokerNtfThirteenRobBankerResultInfo{" +
                "allRobBankerInfo=" + allRobBankerInfo +
                ", bankerPlayerUid=" + bankerPlayerUid +
                '}';
    }
}
