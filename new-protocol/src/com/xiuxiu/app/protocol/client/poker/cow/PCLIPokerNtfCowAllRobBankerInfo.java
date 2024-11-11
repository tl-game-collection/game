package com.xiuxiu.app.protocol.client.poker.cow;

import java.util.HashMap;

/**
 * @auther: yuyunfei
 * @date: 2020/1/7 10:08
 * @comment:
 */
public class PCLIPokerNtfCowAllRobBankerInfo {
    public HashMap<Long/*playerUid*/, Integer/*multiple, -1: 表示还没有抢*/> allInfo = new HashMap<>();

    @Override
    public String toString() {
        return "PCLIPokerNtfCowAllRobBankerInfo{" +
                "allInfo=" + allInfo +
                '}';
    }
}
