package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerReqTakeInfo {
    public List<Byte> cards = new ArrayList<>();    // 0-54
    public List<Byte> laiziCards = null;
    public int cardType;                            // 1: 单牌, 2: 对子, 3: 三张, 4: 顺子, 5: 连对, 6: 飞机, 7: 三带一, 8: 三带二, 9: 四带三, 10: 炸弹

    @Override
    public String toString() {
        return "PCLIPokerReqTakeInfo{" +
                "cards=" + cards +
                ", laiziCards=" + laiziCards +
                ", cardType=" + cardType +
                '}';
    }
}
