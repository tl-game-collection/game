package com.xiuxiu.app.protocol.client.poker.cow;

import java.util.HashMap;

/**
 * @auther: yuyunfei
 * @date: 2020/1/7 10:02
 * @comment:
 */
public class PCLIPokerNtfCowRobBankerResultInfo {
    public HashMap<Long/*playerUid*/, Integer/*multiple*/> allRobBankerInfo = new HashMap<>();      // 自由抢庄模式暗抢时才生效
    public long bankerPlayerUid;        // 庄家玩家uid
    //端火锅 add
    public int hotBankerLoop;//当前庄进行的轮次；
    public int hotDeskNote;//当前桌面筹码；

    @Override
    public String toString() {
        return "PCLIPokerNtfCowRobBankerResultInfo{" +
                "allRobBankerInfo=" + allRobBankerInfo +
                ", bankerPlayerUid=" + bankerPlayerUid +
                ", hotBankerLoop=" + hotBankerLoop +
                ", hotDeskNote=" + hotDeskNote +
                '}';
    }
}
