package com.xiuxiu.app.protocol.client.mahjong;

public class PCLIMahjongReqEatInfo {
    public byte cardValue;       // 吃牌
    public byte type;            // 1: 前, 2: 中, 3: 后

    @Override
    public String toString() {
        return "PCLIMahjongReqEatInfo{" +
                "cardValue=" + cardValue +
                ", type=" + type +
                '}';
    }
}
