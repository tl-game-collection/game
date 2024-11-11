package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfLandLordMultiple {
    public long playerUid; // 玩家UID
    public int value; // 加倍值：0-不加倍，2-2倍，4-4倍
    public boolean allCalled; // 是否所有人都叫过了

    @Override
    public String toString() {
        return "PCLIPokerNtfLandLordMultiple{" +
                "playerUid=" + playerUid +
                ", value=" + value +
                ", allCalled=" + allCalled +
                '}';
    }
}
