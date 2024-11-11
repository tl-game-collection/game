package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfPaiGowAllOpenInfo {
    public List<Long> allOpenCardUids = new ArrayList<>();
    public List<Byte> openCards = new ArrayList<>();

    @Override
    public String toString() {
        return "PCLIPokerNtfPaiGowAllOpenInfo{" +
                ", allOpenCardUids=" + allOpenCardUids +
                ", openCards=" + openCards +
                '}';
    }
}
