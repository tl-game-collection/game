package com.xiuxiu.algorithm.poker;

import java.util.LinkedList;
import java.util.List;

public class PokerHandCard {
    protected List<Byte> nHandCardList = new LinkedList<>();
    protected byte[] aHandCardList = new byte[15];              // 3, 4, 5, 6, 7, 8, 9, 10, j, q, k, a, 2, 小王, 大王
    protected byte takeCnt = 0;

    public void init() {

    }

    public void addHandCard(byte card) {
        this.nHandCardList.add(card);
        ++this.aHandCardList[PokerUtil.getCardValue(card)];
    }

    public boolean setHandCard(byte card, byte newCard) {
        int index = this.nHandCardList.indexOf(card);
        if (-1 != index) {
            this.nHandCardList.set(index, newCard);
            --this.aHandCardList[PokerUtil.getCardValue(card)];
            ++this.aHandCardList[PokerUtil.getCardValue(newCard)];
            return true;
        }
        return false;
    }

    public boolean hasCard(byte card) {
        return -1 != this.nHandCardList.indexOf(card);
    }

    public void delHandCard(byte card) {
        if (this.nHandCardList.remove((Byte) card)) {
            --this.aHandCardList[PokerUtil.getCardValue(card)];
            ++takeCnt;
        }
    }

    public boolean verifyCard(List<Byte> card) {
        if (card.size() < 1) {
            return true;
        }
        PokerUtil.sort(card);
        // TODO 可优化
        int len2 = this.nHandCardList.size();
        for (int i = 0, len = card.size(), j = 0; i < len; ++i) {
            for (j = 0; j < len2; ++j) {
                if (card.get(i) == this.nHandCardList.get(j)) {
                    break;
                }
            }
            if (j == len2) {
                return false;
            }
        }
        return true;
    }

    public boolean hasBomb() {
        for (int i = 0, len = this.aHandCardList.length; i < len; ++i) {
            if (4 == this.aHandCardList[i]) {
                return true;
            }
        }
        return false;
    }

    public boolean isBomb(byte card) {
        byte cardValue = PokerUtil.getCardValue(card);
        return 4 == this.aHandCardList[cardValue];
    }

    public List<Byte> getHandCard() {
        return this.nHandCardList;
    }

    public byte[] getHandCardCnt() {
        return this.aHandCardList;
    }

    public byte getTakeCnt() {
        return this.takeCnt;
    }

    public void clear() {
        this.nHandCardList.clear();
        for (int i = 0; i < 15; ++i) {
            this.aHandCardList[i] = 0;
        }
        this.takeCnt = 0;
    }
}
