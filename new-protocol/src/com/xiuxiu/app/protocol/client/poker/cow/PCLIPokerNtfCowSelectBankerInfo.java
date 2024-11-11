package com.xiuxiu.app.protocol.client.poker.cow;

/**
 * @auther: yuyunfei
 * @date: 2020/1/7 10:12
 * @comment:
 */
public class PCLIPokerNtfCowSelectBankerInfo {
    public long selectPlayerUid;        // 庄家玩家uid

    @Override
    public String toString() {
        return "PCLIPokerNtfCowSelectBankerInfo{" +
                "selectPlayerUid=" + selectPlayerUid +
                '}';
    }
}
