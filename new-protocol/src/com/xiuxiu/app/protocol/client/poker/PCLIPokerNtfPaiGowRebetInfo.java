package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfPaiGowRebetInfo {
    public long playerUid;
    public List<Integer> rebets = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIPokerNtfPaiGowRebetInfo{" +
                "playerUid=" + playerUid +
                ", rebets=" + rebets +
                '}';
    }
}
