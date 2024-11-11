package com.xiuxiu.app.protocol.client.room;

import java.util.List;

public class PCLIRoomNtfBeginInfoByPoker extends PCLIRoomNtfBeginInfo {
    public int myIndex;
    public List<Byte> myCards;
    public int firstTakeIndex;      // 先出牌位置
    public byte firstTakeCard;      // 先出牌值
    public List<Byte> laiziCards;   //癞子牌；
    public int leftCardSize;        //剩余多少张

    @Override
    public String toString() {
        return "PCLIRoomNtfBeginInfoByPoker{" +
                "myIndex=" + myIndex +
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
