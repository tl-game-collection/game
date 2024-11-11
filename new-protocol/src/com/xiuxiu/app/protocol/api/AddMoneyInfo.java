package com.xiuxiu.app.protocol.api;

public class AddMoneyInfo {
    protected long playerUid;
    protected int moneyType;
    protected int moneyValue;

    public long getPlayerUid() {
        return playerUid;
    }

    public void setPlayerUid(long playerUid) {
        this.playerUid = playerUid;
    }

    public int getMoneyType() {
        return moneyType;
    }

    public void setMoneyType(int moneyType) {
        this.moneyType = moneyType;
    }

    public int getMoneyValue() {
        return moneyValue;
    }

    public void setMoneyValue(int moneyValue) {
        this.moneyValue = moneyValue;
    }

    @Override
    public String toString() {
        return "AddMoneyInfo{" +
                "playerUid=" + playerUid +
                ", moneyType=" + moneyType +
                ", moneyValue=" + moneyValue +
                '}';
    }
}
