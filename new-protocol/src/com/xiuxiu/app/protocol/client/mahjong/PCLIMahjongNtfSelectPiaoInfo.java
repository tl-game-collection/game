package com.xiuxiu.app.protocol.client.mahjong;

public class PCLIMahjongNtfSelectPiaoInfo {
    public int op;          // 0: 不需要选飘 1: 需要选飘

    @Override
    public String toString() {
        return "PCLIMahjongNtfSelectPiaoInfo{" +
                "op=" + op +
                '}';
    }
}
