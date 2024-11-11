package com.xiuxiu.app.protocol.client.poker;

import java.util.HashMap;

public class PCLIPokerNtfBombScoreInfo {
    public HashMap<Long, Integer> bombScore = new HashMap<>();
    public HashMap<Long, String> totalScore = new HashMap<>();

    @Override
    public String toString() {
        return "PCLIPokerNtfBombScoreInfo{" +
                "bombScore=" + bombScore +
                ", totalScore=" + totalScore +
                '}';
    }
}
