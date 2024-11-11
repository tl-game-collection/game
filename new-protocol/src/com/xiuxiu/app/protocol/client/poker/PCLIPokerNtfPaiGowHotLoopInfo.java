package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfPaiGowHotLoopInfo {
    public int curLoop;                     // 当前回合数
    public int curNote;                     // 当前锅底

    @Override
    public String toString() {
        return "PCLIPokerNtfPaiGowHotLoopInfo{" +
                "curLoop=" + curLoop +
                ", curNote=" + curNote +
                '}';
    }
}
