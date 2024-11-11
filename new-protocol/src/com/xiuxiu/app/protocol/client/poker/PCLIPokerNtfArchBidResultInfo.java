package com.xiuxiu.app.protocol.client.poker;

import java.util.List;

public class PCLIPokerNtfArchBidResultInfo {
    public long bankerUid; // 庄家UID
    public int contract; // 定约，31-3人局独庄，32-3人局抄庄，41-4人局独庄，42-4人局2对2
    public List<Byte> reservedCards; // 底牌

    @Override
    public String toString() {
        return "PCLIPokerNtfArchBidResultInfo{" +
                "bankerUid=" + bankerUid +
                ", contract=" + contract +
                ", reservedCards=" + reservedCards +
                '}';
    }
}
