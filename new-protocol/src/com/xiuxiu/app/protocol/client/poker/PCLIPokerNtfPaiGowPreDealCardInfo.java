package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfPaiGowPreDealCardInfo {
    public List<Byte> preDealCard = new ArrayList<>();//牌；

    @Override
    public String toString() {
        return "PCLIPokerNtfPaiGowPreDealCardInfo{" +
                "preDealCard=" + preDealCard +
                '}';
    }
}
