package com.xiuxiu.app.protocol.client.poker;

import java.util.List;

public class PCLIPokerNtfLastCardInfo {
    public long PlayerUid;
    public List<Byte> cards;   // 0-54

    @Override
    public String toString() {
        return "PCLIPokerNtfLastCardInfo{" +
                "PlayerUid=" + PlayerUid +
                ", cards=" + cards +
                '}';
    }
}
