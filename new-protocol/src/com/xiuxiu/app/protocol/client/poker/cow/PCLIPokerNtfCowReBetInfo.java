package com.xiuxiu.app.protocol.client.poker.cow;

/**
 * @auther: yuyunfei
 * @date: 2020/1/7 10:09
 * @comment:
 */
public class PCLIPokerNtfCowReBetInfo {
    public long playerUid;
    public int rebet;       // 下注

    @Override
    public String toString() {
        return "PCLIPokerNtfCowReBetInfo{" +
                "playerUid=" + playerUid +
                ", rebet=" + rebet +
                '}';
    }
}
