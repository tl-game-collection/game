package com.xiuxiu.app.protocol.client.room;

import java.util.Arrays;

public class PCLIPaiGowRobRoomNtfBeginInfoByPoker extends PCLIPaiGowRoomNtfBeginInfoByPoker {
    public long prevBankerUid;      // 上一把庄家UID
    public int prevScore;           // 上一把赢分，为0表示上把无输赢
    public int robBankerMultiple;   // 抢庄倍数，为0表示未参与抢庄

    @Override
    public String toString() {
        return "PCLIPaiGowRobRoomNtfBeginInfoByPoker{" +
                "prevBankerUid=" + prevBankerUid +
                ", prevScore=" + prevScore +
                ", robBankerMultiple=" + robBankerMultiple +
                ", crap1=" + crap1 +
                ", crap2=" + crap2 +
                ", defaultCards=" + defaultCards +
                ", defaultType=" + Arrays.toString(defaultType) +
                ", bankerCards=" + bankerCards +
                ", hotNote=" + hotNote +
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
