package com.xiuxiu.app.protocol.client.mahjong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PCLIMahjongNtfYangPaiInfo {
    public long playerUid; // 玩家UID
    public List<Byte> cards = new ArrayList<>(); // 仰的牌
    public HashMap<Byte, HashMap<Byte, Integer>> tingInfo = new HashMap();

    @Override
    public String toString() {
        return "PCLIMahjongNtfYangPaiInfo{" +
                "playerUid=" + playerUid +
                ", cards=" + cards +
                ", tingInfo=" + tingInfo +
                '}';
    }
}
