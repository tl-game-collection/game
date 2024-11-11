package com.xiuxiu.app.protocol.client.poker;

import java.util.ArrayList;
import java.util.List;

public class PCLIPokerNtfSendRedEnvelopeList {
    @Override
    public String toString() {
        return "PCLIPokerNtfSendRedEnvelopeList{" +
                "sendPlayerList=" + sendPlayerList +
                '}';
    }

    public List<PCLIPokerReqSendRedEnvelope> sendPlayerList = new ArrayList<>();            // 所有发红包玩家

}
