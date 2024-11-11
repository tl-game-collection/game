package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PCLIPokerNtfPaiGowOpenInfo {
    public long playerUid;
    public List<Byte> card = new ArrayList<>();//牌；
    public int[] cardType = new int[2];

    @Override
    public String toString() {
        return "PCLIPokerNtfPaiGowOpenInfo{" +
                "playerUid=" + playerUid +
                ", card=" + card +
                ", cardType=" + Arrays.toString(cardType) +
                '}';
    }
}
