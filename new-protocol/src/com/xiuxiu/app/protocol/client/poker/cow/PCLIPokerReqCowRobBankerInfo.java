package com.xiuxiu.app.protocol.client.poker.cow;

/**
 * @auther: yuyunfei
 * @date: 2020/1/7 10:25
 * @comment:
 */
public class PCLIPokerReqCowRobBankerInfo {
    public int mul;         // 抢庄倍数, 0: 不抢庄

    @Override
    public String toString() {
        return "PCLIPokerReqCowRobBankerInfo{" +
                "mul=" + mul +
                '}';
    }
}
