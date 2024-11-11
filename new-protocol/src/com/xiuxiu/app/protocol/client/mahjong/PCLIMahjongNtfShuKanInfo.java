package com.xiuxiu.app.protocol.client.mahjong;

public class PCLIMahjongNtfShuKanInfo {
    public int type;    // 1: 2,5,8将, 2: 1-9点数 3: 二者

    @Override
    public String toString() {
        return "PCLIMahjongNtfShuKanInfo{" +
                "type=" + type +
                '}';
    }
}
