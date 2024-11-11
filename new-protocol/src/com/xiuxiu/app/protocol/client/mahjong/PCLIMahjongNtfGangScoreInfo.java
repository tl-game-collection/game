package com.xiuxiu.app.protocol.client.mahjong;

import java.util.HashMap;

public class PCLIMahjongNtfGangScoreInfo {
    public HashMap<Long/*PlayerUid*/, Integer/*Score*/> gangScore = new HashMap<>();
    public HashMap<Long/*PlayerUid*/, String/*Score*/> totalScore = new HashMap<>();
    public boolean hjzy = false;            // 是否是呼叫转移

    @Override
    public String toString() {
        return "PCLIMahjongNtfGangScoreInfo{" +
                "gangScore=" + gangScore +
                ", totalScore=" + totalScore +
                ", hjzy=" + hjzy +
                '}';
    }
}
