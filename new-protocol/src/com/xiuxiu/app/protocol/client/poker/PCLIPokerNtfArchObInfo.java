package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfArchObInfo {
    public long playerUid;
    public List<Byte> cards = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIPokerNtfArchObInfo{" +
                "playerUid=" + playerUid +
                ", cards=" + cards +
                '}';
    }
}
