package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfShowCardInfo {
    public long PlayerUid;
    public List<Byte> cards = new ArrayList<>();    // 0-54
    @Override
    public String toString() {
        return "PCLIPokerNtfShowCardInfo{" +
                "PlayerUid=" + PlayerUid +
                ", cards=" + cards +
                '}';
    }
}
