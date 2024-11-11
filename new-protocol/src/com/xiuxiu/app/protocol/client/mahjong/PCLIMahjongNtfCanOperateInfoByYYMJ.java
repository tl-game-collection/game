package com.xiuxiu.app.protocol.client.mahjong;

public class PCLIMahjongNtfCanOperateInfoByYYMJ extends PCLIMahjongNtfCanOperateInfo {
    public boolean ting; // 是否能报听

    public PCLIMahjongNtfCanOperateInfoByYYMJ() {
    }

    public PCLIMahjongNtfCanOperateInfoByYYMJ(boolean bump, boolean bar, boolean hu, boolean eat, boolean ting, byte card) {
        super(bump, bar, hu, eat, card);
        this.ting = ting;
    }

    @Override
    public String toString() {
        return "PCLIMahjongNtfCanOperateInfoByYYMJ{" +
                "ting=" + ting +
                ", bump=" + bump +
                ", bar=" + bar +
                ", hu=" + hu +
                ", eat=" + eat +
                ", card=" + card +
                ", allCard=" + allCard +
                '}';
    }
}
