package com.xiuxiu.app.protocol.client.mahjong;

import java.util.HashMap;

public class PCLIMahjongHalfBrightInfo {
    public HashMap<Byte, Integer> huCard = new HashMap<>();

    @Override
    public String toString() {
        return "PCLIMahjongHalfBrightInfo{" +
                "huCard=" + huCard +
                '}';
    }
}
