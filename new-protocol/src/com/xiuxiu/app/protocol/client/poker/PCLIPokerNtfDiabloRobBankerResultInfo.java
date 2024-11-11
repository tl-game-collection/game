package com.xiuxiu.app.protocol.client.poker;

import java.util.HashMap;

public class PCLIPokerNtfDiabloRobBankerResultInfo {
    public HashMap<Long/*playerUid*/, Integer/*multiple*/> allRobBankerInfo = new HashMap<>();      // 抢庄
    public long bankerPlayerUid;        // 庄家玩家uid

    @Override
    public String toString() {
        return "PCLIPokerNtfDiabloRobBankerResultInfo{" +
                "allRobBankerInfo=" + allRobBankerInfo +
                ", bankerPlayerUid=" + bankerPlayerUid +
                '}';
    }
}
