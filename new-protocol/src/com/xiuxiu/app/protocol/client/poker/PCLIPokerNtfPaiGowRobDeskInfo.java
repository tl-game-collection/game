package com.xiuxiu.app.protocol.client.poker;

import java.util.Arrays;

public class PCLIPokerNtfPaiGowRobDeskInfo extends PCLIPokerNtfPaiGowDeskInfo {
    public long prevBankerUid;      // 上一把庄家UID
    public int prevScore;           // 上一把赢分，为0表示上把无输赢
    public int robBankerMultiple;   // 抢庄倍数，为0表示未参与抢庄
    public int surplusTime;         // 剩余时间

    @Override
    public String toString() {
        return "PCLIPokerNtfPaiGowRobDeskInfo{" +
                "prevBankerUid=" + prevBankerUid +
                ", prevScore=" + prevScore +
                ", robBankerMultiple=" + robBankerMultiple +
                ", curTakeIndex=" + curTakeIndex +
                ", lastTakePlayerUid=" + lastTakePlayerUid +
                ", lastTakeCard=" + lastTakeCard +
                ", card=" + card +
                ", allScore=" + allScore +
                ", allOnlineState=" + allOnlineState +
                ", defaultCards=" + defaultCards +
                ", defaultType=" + Arrays.toString(defaultType) +
                ", preDealCard=" + preDealCard +
                ", openCard=" + openCard +
                ", openCardType=" + Arrays.toString(openCardType) +
                ", allRebet=" + allRebet +
                ", allRobBank=" + allRobBank +
                ", isOpenCards=" + isOpenCards +
                ", openCards=" + openCards +
                ", curHotDeskNote=" + curHotDeskNote +
                ", keepHotCount=" + keepHotCount +
                ", bankerShowCards=" + bankerShowCards +
                ", bankerBureau=" + bankerBureau +
                ", roomInfo=" + roomInfo +
                ", bankerPlayerUid=" + bankerPlayerUid +
                ", bankerIndex=" + bankerIndex +
                ", curBureau=" + curBureau +
                ", gameing=" + gameing +
                ", surplusTime=" + surplusTime +
                '}';
    }
}
