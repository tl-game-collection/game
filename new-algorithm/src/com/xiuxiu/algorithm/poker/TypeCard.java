package com.xiuxiu.algorithm.poker;

import java.util.ArrayList;
import java.util.List;

public class TypeCard {
    public List<Byte> cardList = new ArrayList<>();//手牌
    public EPokerCardType cardType;//牌型

    public void setCardType(EPokerCardType cardType) {
        this.cardType = cardType;
    }

    public EPokerCardType getCardType() {
        return cardType;
    }

    public List<Byte> getCardList() {
        return cardList;
    }

    public void setCardList(List<Byte> cardList) {
        this.cardList = cardList;
    }
}

