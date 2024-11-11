package com.xiuxiu.app.protocol.client.mahjong;

public class PCLIMahjongNtfFumbleInfoYCXL extends PCLIMahjongNtfFumbleInfo {
    public boolean bar;

    @Override
    public String toString() {
        return "PCLIMahjongNtfFumbleInfoYCXL{" +
                "uid=" + uid +
                ", index=" + index +
                ", value=" + value +
                ", bar=" + bar +
                ", remainCard=" + remainCard +
                ", auto=" + auto +
                ", handCard=" + handCard +
                ", tingInfo=" + tingInfo +
                '}';
    }
}
