package com.xiuxiu.app.protocol.api.temp.player;

public class PCLIWhiteListInfo {
    public boolean PokerAndMajong;  //扑克和麻将
    public boolean NiuNiu;          //牛牛
    public boolean JinHua;          //金花
    public boolean SanGong;          //三公

    @Override
    public String toString() {
        return "PCLIWhiteListInfo{" +
                "PokerAndMajong=" + PokerAndMajong +
                ", NiuNiu=" + NiuNiu +
                ", JinHua=" + JinHua +
                ", SanGong=" + SanGong +
                '}';
    }
}
