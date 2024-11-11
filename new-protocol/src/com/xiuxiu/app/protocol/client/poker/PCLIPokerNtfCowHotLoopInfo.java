package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfCowHotLoopInfo {
    public int curLoop;                     // 当前回合数
    public int curNote;                     // 当前锅底
    public int curKeepCount;                // 当前续锅次数

    @Override
    public String toString() {
        return "PCLIPokerNtfCowHotLoopInfo{" +
                "curLoop=" + curLoop +
                ", curNote=" + curNote +
                ", curKeepCount=" + curKeepCount +
                '}';
    }
}
