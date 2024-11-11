package com.xiuxiu.app.protocol.client.poker;

import java.util.HashMap;

public class PCLIPokerNtfSGRobBankerResultInfo {
    public HashMap<Long/*playerUid*/, Integer/*multiple*/> allRobBankerInfo = new HashMap<>();      // 自由抢庄模式暗抢时才生效
    public long bankerPlayerUid;        // 庄家玩家uid


    @Override
    public String toString() {
        return "PCLIPokerNtfSGRobBankerResultInfo{" +
                "allRobBankerInfo=" + allRobBankerInfo +
                ", bankerPlayerUid=" + bankerPlayerUid +
                '}';
    }
}
