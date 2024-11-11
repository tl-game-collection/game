package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfSGRebetBeginInfo {
    public int baseRebet = 0;                   // 基础加注
    public boolean doubling = false;            // 加倍
    public int pushNote = 0;                    // 推注数 0: 表示不能推注
    public boolean isPushNote =false;

    @Override
    public String toString() {
        return "PCLIPokerNtfSGRebetBeginInfo{" +
                "baseRebet=" + baseRebet +
                ", doubling=" + doubling +
                ", pushNote=" + pushNote +
                ", isPushNote=" + isPushNote +
                '}';
    }
}
