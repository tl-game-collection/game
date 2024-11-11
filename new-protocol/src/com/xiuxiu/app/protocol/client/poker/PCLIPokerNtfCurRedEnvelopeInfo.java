package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfCurRedEnvelopeInfo {
    @Override
    public String toString() {
        return "PCLIPokerNtfCurRedEnvelopeInfo{" +
                "sengPlayerUid=" + sengPlayerUid +
                ", sum=" + sum +
                ", digit=" + digit +
                '}';
    }

    public long sengPlayerUid;                 // 发送红包玩家
    public int sum;                            // 当前红包金额
    public int digit = 0;                      // 当前埋雷数字

}
