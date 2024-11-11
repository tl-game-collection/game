package com.xiuxiu.app.protocol.client.poker.cow;

/**
 * @auther: yuyunfei
 * @date: 2020/1/7 10:34
 * @comment:
 */
public class PCIPokerReqSelectRebetInfo {
    public long groupUid;//群uid
    public long arenaUid;//竞技场uid
    @Override
    public String toString() {
        return "PCIPokerReqSelectRebetInfo{" +
                "groupUid=" + groupUid +
                "arenaUid=" + arenaUid +
                '}';
    }
}
