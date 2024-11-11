package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfSGCardInfo {

    public List<Byte> card = new ArrayList<>();                  // 牌

    public int cardType;

    @Override
    public String toString() {
        return "PCLIPokerNtfSGCardInfo{" +
                "card=" + card +
                ", cardType=" + cardType +
                '}';
    }
}
