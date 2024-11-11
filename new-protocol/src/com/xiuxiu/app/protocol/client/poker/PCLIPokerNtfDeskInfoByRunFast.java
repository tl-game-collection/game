package com.xiuxiu.app.protocol.client.poker;

import java.util.HashMap;
import java.util.Map;

public class PCLIPokerNtfDeskInfoByRunFast extends PCLIPokerNtfDeskInfo {
    public long redPeachTenUid = -1;        // 红桃十Uid
    public HashMap<Long, Boolean> autoMode = new HashMap<>();   // 是否托管
    public Map<Long, Integer> piaoScore = new HashMap<>();
    public boolean isPrimula;       // 是否叫春

    @Override
    public String toString() {
        return "PCLIPokerNtfDeskInfoByRunFast{" +
                "redPeachTenUid=" + redPeachTenUid +
                ", card=" + card +
                ", otherCardCnt=" + otherCardCnt +
                ", allScore=" + allScore +
                ", allOnlineState=" + allOnlineState +
                ", lastTakeCard=" + lastTakeCard +
                ", lastTakePlayerUid=" + lastTakePlayerUid +
                ", roomInfo=" + roomInfo +
                ", bankerPlayerUid=" + bankerPlayerUid +
                ", bankerIndex=" + bankerIndex +
                ", curBureau=" + curBureau +
                ", autoMode=" + autoMode +
                ", piaoScore=" + piaoScore +
                ", isPrimula=" + isPrimula +
                ", gameing=" + gameing +
                '}';
    }
}
