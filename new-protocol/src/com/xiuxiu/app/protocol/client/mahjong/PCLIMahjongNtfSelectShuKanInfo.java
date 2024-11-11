package com.xiuxiu.app.protocol.client.mahjong;

import java.util.HashMap;
import java.util.List;

public class PCLIMahjongNtfSelectShuKanInfo {
    public HashMap<Long/*playerUid*/, List<Integer>/*数坎, -1:表示还没, 高16位258将, 低16为1-9点数*/> allShuKan = new HashMap<>();

    @Override
    public String toString() {
        return "PCLIMahjongNtfSelectShuKanInfo{" +
                "allShuKan=" + allShuKan +
                '}';
    }
}
