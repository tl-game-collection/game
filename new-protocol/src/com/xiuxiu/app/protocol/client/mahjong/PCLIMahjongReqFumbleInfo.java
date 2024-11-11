package com.xiuxiu.app.protocol.client.mahjong;

public class PCLIMahjongReqFumbleInfo {
    public long uid;
    public int index;
    public byte value;
    public int remainCard;

    @Override
    public String toString() {
        return "PCLIMahjongReqFumbleInfo{" +
                "uid=" + uid +
                ", index=" + index +
                ", value=" + value +
                ", remainCard=" + remainCard +
                '}';
    }
}
