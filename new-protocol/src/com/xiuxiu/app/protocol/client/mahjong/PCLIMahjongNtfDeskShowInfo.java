package com.xiuxiu.app.protocol.client.mahjong;

import java.util.HashMap;

public class PCLIMahjongNtfDeskShowInfo {
    public HashMap<Long, Long> allShow = new HashMap<>();

    @Override
    public String toString() {
        return "PCLIMahjongNtfDeskShowInfo{" +
                "allShow=" + allShow +
                '}';
    }
}
