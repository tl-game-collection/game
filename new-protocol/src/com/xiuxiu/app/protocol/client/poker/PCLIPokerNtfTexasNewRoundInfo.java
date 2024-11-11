package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfTexasNewRoundInfo {
    public int round; // 轮次，从1开始
    public long playerUidToBet; // 说话人的玩家UID
    public List<Byte> cards = new ArrayList<>();
    @Override
    public String toString() {
        return "PCLIPokerNtfTexasNewRoundInfo{" +
                "round=" + round +
                ", playerUidToBet=" + playerUidToBet +
                 ", carsds=" + cards +
                '}';
    }
}
