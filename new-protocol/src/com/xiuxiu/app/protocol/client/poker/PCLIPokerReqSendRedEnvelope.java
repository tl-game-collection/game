package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerReqSendRedEnvelope {
    public int sum;                            // 红包金额
    public int digit = 0;                      // 埋雷数字

    @Override
    public String toString() {
        return "PCLIPokerReqSendRedEnvelope{" +
                "sum=" + sum +
                ", digit=" + digit +
                '}';
    }
}
