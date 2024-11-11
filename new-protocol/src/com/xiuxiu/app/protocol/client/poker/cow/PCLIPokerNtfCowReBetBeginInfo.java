package com.xiuxiu.app.protocol.client.poker.cow;

/**
 * @auther: yuyunfei
 * @date: 2020/1/7 10:03
 * @comment:
 */
public class PCLIPokerNtfCowReBetBeginInfo {
    public int baseRebet = 0;                   // 基础加注
    public boolean doubling = false;            // 加倍
    public int pushNote = 0;                    // 推注数 0: 表示不能推注
    public boolean isPushNote =false;

    @Override
    public String toString() {
        return "PCLIPokerNtfCowReBetBeginInfo{" +
                "baseRebet=" + baseRebet +
                ", doubling=" + doubling +
                ", pushNote=" + pushNote +
                ", isPushNote=" + isPushNote +
                '}';
    }
}
