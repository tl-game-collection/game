package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.List;

public class PCLIMahjongNtfCanOperateInfo {
    public boolean bump;
    public boolean bar;
    public boolean hu;
    public boolean eat;
    public byte card;
    public List<Byte> allCard = new ArrayList<>();

    public PCLIMahjongNtfCanOperateInfo() {

    }

    public PCLIMahjongNtfCanOperateInfo(boolean bump, boolean bar, boolean hu, boolean eat, byte card) {
        this.bump = bump;
        this.bar = bar;
        this.hu = hu;
        this.eat = eat;
        this.card = card;
    }

    public PCLIMahjongNtfCanOperateInfo(boolean bump, boolean bar, boolean hu, boolean eat, List<Byte> card) {
        this.bump = bump;
        this.bar = bar;
        this.hu = hu;
        this.eat = eat;
        this.card = card.get(0);
        this.allCard.addAll(card);
    }

    @Override
    public String toString() {
        return "PCLIMahjongNtfCanOperateInfo{" +
                "bump=" + bump +
                ", bar=" + bar +
                ", hu=" + hu +
                ", eat=" + eat +
                ", card=" + card +
                ", allCard=" + allCard +
                '}';
    }
}
