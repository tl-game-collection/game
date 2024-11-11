package com.xiuxiu.app.protocol.client.room;

import java.util.HashMap;
import java.util.Map;

public class PCLIRoomNtfBeginInfoByPokerWithArch extends PCLIRoomNtfBeginInfoByPoker {
    public Map<Long, Integer> playerCardCount = new HashMap<>(); // 玩家手牌数量 <玩家UID， 数量>

    @Override
    public String toString() {
        return "PCLIRoomNtfBeginInfoByPokerWithArch{" +
                "playerCardCount=" + playerCardCount +
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
