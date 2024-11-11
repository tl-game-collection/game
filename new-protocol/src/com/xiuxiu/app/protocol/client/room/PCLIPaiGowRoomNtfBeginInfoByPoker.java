package com.xiuxiu.app.protocol.client.room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PCLIPaiGowRoomNtfBeginInfoByPoker extends PCLIRoomNtfBeginInfoByPoker{
    public int crap1;
    public int crap2;
    public List<Byte> defaultCards = new ArrayList<>();     // 默认提示牌
    public int[] defaultType = new int[2];                  // 默认提示牌类型；
    public List<Byte> bankerCards = new ArrayList<>();      // 庄家需要亮的牌
    public int hotNote;

    @Override
    public String toString() {
        return "PCLIPaiGowRoomNtfBeginInfoByPoker{" +
                "crap1=" + crap1 +
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
