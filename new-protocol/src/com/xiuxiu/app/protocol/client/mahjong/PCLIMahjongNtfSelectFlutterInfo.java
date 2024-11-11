package com.xiuxiu.app.protocol.client.mahjong;

import java.util.HashMap;

public class PCLIMahjongNtfSelectFlutterInfo {
    public HashMap<Long/*playerUid*/, Integer/*飘分, -1:表示还没选飘*/> allFlutter = new HashMap<>();

    @Override
    public String toString() {
        return "PCLIMahjongNtfSelectFlutterInfo{" +
                "allFlutter=" + allFlutter +
                '}';
    }
}
