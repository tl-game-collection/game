package com.xiuxiu.app.protocol.client.room;

import java.util.HashMap;
import java.util.Map;

public class PCLIRoomNtfBeginInfoByPokerWithWuShiK extends PCLIRoomNtfBeginInfoByPoker {
    public Map<Long, Integer> playerCardCount = new HashMap<>(); // 玩家手牌数量 <玩家UID， 数量>
    public int friendIndex = -1;        //  (盟友索引)
    public long friendUid = -1;          //  (盟友ID)
    @Override
    public String toString() {
        return "PCLIRoomNtfBeginInfoByPokerWithWuShiK{" +
                "playerCardCount=" + playerCardCount +
                ", myIndex=" + myIndex +
                ", myCards=" + myCards +
                ", firstTakeIndex=" + firstTakeIndex +
                ", firstTakeCard=" + firstTakeCard +
                ", laiziCards=" + laiziCards +
                ", friendIndex=" + friendIndex +
                ", friendUid=" + friendUid +
                ", leftCardSize=" + leftCardSize +
                ", bankerIndex=" + bankerIndex +
                ", bureau=" + bureau +
                ", roomBriefInfo=" + roomBriefInfo +
                ", d=" + d +
                '}';
    }
}
