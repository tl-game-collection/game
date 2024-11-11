package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerReqPaiGowOpenInfo {
    public List<Byte> card1 = new ArrayList<>();//牌1；
    public List<Byte> card2 = new ArrayList<>();//牌2；

    @Override
    public String toString() {
        return "PCLIPokerNtfPaiGowOpenInfo{" +
                "card1=" + card1 +
                ", card2=" + card2 +
                '}';
    }
}
