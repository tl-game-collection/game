package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfCardInfo {
    public List<Byte> card = new ArrayList<>(); // 牌
    public int cardType;                        // 牌型，32-杂牌，33-高牌，34-对子，35-顺子，36-同花，37-同花顺，38-豹子
    public int cardTypeExtra;                   // 額外牌型

    @Override
    public String toString() {
        return "PCLIPokerNtfCardInfo{" +
                "card=" + card +
                ", cardType=" + cardType +
                ", cardTypeExtra=" + cardTypeExtra +
                '}';
    }
}
