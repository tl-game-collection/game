package com.xiuxiu.app.protocol.client.mahjong;

public class PCLIMahjongNtfFumbleInfoYYMJ extends PCLIMahjongNtfFumbleInfo {
    public boolean ting;

    @Override
    public String toString() {
        return "PCLIMahjongNtfFumbleInfoYYMJ{" +
                "uid=" + uid +
                ", index=" + index +
                ", value=" + value +
                ", ting=" + ting +
                ", remainCard=" + remainCard +
                ", auto=" + auto +
                ", handCard=" + handCard +
                ", tingInfo=" + tingInfo +
                '}';
    }
}
