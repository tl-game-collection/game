package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfArchSortCardInfo {
    public List<Byte> cards = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIPokerNtfArchSortCardInfo{" +
                "cards=" + cards +
                '}';
    }
}
