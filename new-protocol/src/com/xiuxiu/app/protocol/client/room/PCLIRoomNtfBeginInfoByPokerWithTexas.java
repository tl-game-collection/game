package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomNtfBeginInfoByPokerWithTexas extends PCLIRoomNtfBeginInfoByPoker {
    public int pot; // 底池
    public int bankRoll; // 筹码数量
    @Override
    public String toString() {
        return "PCLIRoomNtfBeginInfoByPokerWithStud{" +
                "pot=" + pot +
                ", bankRoll=" + bankRoll +
                ", myIndex=" + myIndex +
                ", myCards=" + myCards +
                ", firstTakeIndex=" + firstTakeIndex +
                ", firstTakeCard=" + firstTakeCard +
                ", laiziCards=" + laiziCards +
                ", leftCardSize=" + leftCardSize +
                ", bankerIndex=" + bankerIndex +
                ", bureau=" + bureau +
                ", roomBriefInfo=" + roomBriefInfo +
                ", d=" + d +
                '}';
    }
}
