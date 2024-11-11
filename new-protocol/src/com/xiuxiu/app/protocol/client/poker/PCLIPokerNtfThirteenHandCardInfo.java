package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfThirteenHandCardInfo {
    public List<Byte> handCard = new ArrayList<>();

    public int sendCardCount = 1;

    @Override
    public String toString() {
        return "PCLIPokerNtfThirteenHandCardInfo{" +
                "handCard=" + handCard +
                ", sendCardCount=" + sendCardCount +
                '}';
    }
}
