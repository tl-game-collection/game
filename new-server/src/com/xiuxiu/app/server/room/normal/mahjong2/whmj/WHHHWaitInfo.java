package com.xiuxiu.app.server.room.normal.mahjong2.whmj;

import com.xiuxiu.app.server.room.normal.mahjong2.action.MahjongWaitAction;

class WHHHWaitInfo extends MahjongWaitAction.WaitInfo {
    private byte card;
    private WHHHWaitInfo extraWait;

    WHHHWaitInfo(byte card) {
        this.card = card;
    }

    byte getCard() {
        return card;
    }

    void setCard(byte card) {
        this.card = card;
    }

    public WHHHWaitInfo getExtraWait() {
        return extraWait;
    }

    public void setExtraWait(WHHHWaitInfo extraWait) {
        this.extraWait = extraWait;
    }

    @Override
    public boolean isBump() {
        return bump || (this.extraWait != null && this.extraWait.isBump());
    }

    @Override
    public boolean isBar() {
        return bar || (this.extraWait != null && this.extraWait.isBar());
    }

    @Override
    public boolean isEat() {
        return eat || (this.extraWait != null && this.extraWait.isEat());
    }

    @Override
    public boolean isHu() {
        return hu || (this.extraWait != null && this.extraWait.isHu());
    }
}
