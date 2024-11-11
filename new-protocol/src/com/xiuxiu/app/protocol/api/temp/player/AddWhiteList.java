package com.xiuxiu.app.protocol.api.temp.player;

public class AddWhiteList {
    public long playerUid;
    public boolean PokerAndMajong;  //扑克和麻将
    public boolean NiuNiu;          //牛牛
    public boolean JinHua;          //金花
    public boolean SanGong;          //三公
    public String sign;             //playerUid + PokerAndMajong + NiuNiu + JinHua + SanGong + key

    @Override
    public String toString() {
        return "AddWhiteList{" +
                "playerUid=" + playerUid +
                ", PokerAndMajong=" + PokerAndMajong +
                ", NiuNiu=" + NiuNiu +
                ", JinHua=" + JinHua +
                ", SanGong=" + SanGong +
                ", sign='" + sign + '\'' +
                '}';
    }
}