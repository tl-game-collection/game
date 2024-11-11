package com.xiuxiu.app.protocol.client.poker.cow;

/**
 * @auther: yuyunfei
 * @date: 2020/1/7 10:34
 * @comment:
 */
public class PCIPokerNtfSelectRebetInfo {
    public boolean isMaxRebet;//是否推最大注
    public int type;//牌型
    public int rank;//排名
    @Override
    public String toString() {
        return "PCIPokerNtfSelectRebetInfo{" +
                "isMaxRebet=" + isMaxRebet +
                "type=" + isMaxRebet +
                "rank=" + rank +
                '}';
    }
}
