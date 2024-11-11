package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerReqLandLordMultiple {
    public int value; // 加倍值：0-不加倍，2-2倍，4-4倍

    @Override
    public String toString() {
        return "PCLIPokerReqLandLordMultiple{" +
                "value=" + value +
                '}';
    }
}
