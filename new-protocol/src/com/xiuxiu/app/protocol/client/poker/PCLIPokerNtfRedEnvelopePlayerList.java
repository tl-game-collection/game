package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfRedEnvelopePlayerList {
    @Override
    public String toString() {
        return "PCLIPokerNtfRedEnvelopePlayerList{" +
                "playerList=" + playerList +
                '}';
    }

    public List<Long> playerList = new ArrayList<>();            // 玩家列表
}
