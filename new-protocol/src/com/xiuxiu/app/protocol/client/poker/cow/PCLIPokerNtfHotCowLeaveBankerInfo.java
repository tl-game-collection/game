package com.xiuxiu.app.protocol.client.poker.cow;

/**
 * @auther: yuyunfei
 * @date: 2020/1/7 10:21
 * @comment:
 */
public class PCLIPokerNtfHotCowLeaveBankerInfo {
    public long playerUid;
    public String score;

    @Override
    public String toString() {
        return "PCLIPokerNtfHotCowLeaveBankerInfo{" +
                "playerUid=" + playerUid +
                ", score='" + score + '\'' +
                '}';
    }
}
