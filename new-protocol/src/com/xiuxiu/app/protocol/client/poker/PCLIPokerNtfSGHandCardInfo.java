package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfSGHandCardInfo {
    public List<Byte> handCard = new ArrayList<>();

    public int sendCardCount = 1;

    @Override
    public String toString() {
        return "PCLIPokerNtfSGHandCardInfo{" +
                "handCard=" + handCard +
                ", sendCardCount=" + sendCardCount +
                '}';
    }
}
