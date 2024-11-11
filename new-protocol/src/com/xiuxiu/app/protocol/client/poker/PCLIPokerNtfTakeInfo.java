package com.xiuxiu.app.protocol.client.poker;

import java.util.List;

public class PCLIPokerNtfTakeInfo {
    public long takePlayerUid;
    public List<Byte> cards;   // 0-54
    public List<Byte> laiziCards;//数组里放的是癞子坐成的什么牌
    public int cardType;                            // 1: 单牌, 2: 对子, 3: 三张, 4: 顺子, 5: 连对, 6: 飞机, 7: 三带一, 8: 三带二, 9: 四带三, 10: 炸弹
    public long nextTakePlayerUid;  // 下一个出牌玩家uid

    public PCLIPokerNtfTakeInfo() {

    }

    public PCLIPokerNtfTakeInfo(long takePlayerUid, List<Byte> cards, int cardType, long nextTakePlayerUid) {
        this.takePlayerUid = takePlayerUid;
        this.cards = cards;
        this.cardType = cardType;
        this.nextTakePlayerUid = nextTakePlayerUid;
        this.laiziCards = laiziCards;
    }

    public PCLIPokerNtfTakeInfo(long takePlayerUid, List<Byte> cards, int cardType, long nextTakePlayerUid, List<Byte> laiziCards) {
        this.takePlayerUid = takePlayerUid;
        this.cards = cards;
        this.cardType = cardType;
        this.nextTakePlayerUid = nextTakePlayerUid;
        this.laiziCards = laiziCards;
    }

    @Override
    public String toString() {
        return "PCLIPokerNtfTakeInfo{" +
                "takePlayerUid=" + takePlayerUid +
                ", cards=" + cards +
                ", laiziCards=" + laiziCards +
                ", cardType=" + cardType +
                ", nextTakePlayerUid=" + nextTakePlayerUid +
                '}';
    }
}
