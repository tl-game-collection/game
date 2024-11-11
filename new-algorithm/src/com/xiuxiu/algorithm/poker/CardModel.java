package com.xiuxiu.algorithm.poker;

import java.util.ArrayList;
import java.util.List;
// 十三水 自动理牌
public class CardModel
{
    public List<TypeCard> typeCardList = new ArrayList<>();

    public List<TypeCard> getTypeCardList() {
        return typeCardList;
    }

    public void setTypeCardList(List<TypeCard> typeCardList) {
        this.typeCardList = typeCardList;
    }
}