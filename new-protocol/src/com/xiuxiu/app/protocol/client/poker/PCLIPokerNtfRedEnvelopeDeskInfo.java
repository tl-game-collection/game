package com.xiuxiu.app.protocol.client.poker;

import com.xiuxiu.app.protocol.client.room.PCLIRoomDeskInfo;

import java.util.HashMap;

public class PCLIPokerNtfRedEnvelopeDeskInfo extends PCLIRoomDeskInfo {
    public PCLIPokerNtfCurRedEnvelopeInfo curRedEnvelopeInfo = new PCLIPokerNtfCurRedEnvelopeInfo();       // 当前红包信息
    public HashMap<Long, Integer> robPlayerList = new HashMap<>();  // 当前抢红包信息

    @Override
    public String toString() {
        return "PCLIPokerNtfRedEnvelopeDeskInfo{" +
                "curRedEnvelopeInfo=" + curRedEnvelopeInfo +
                ", robPlayerList=" + robPlayerList +
                ", roomInfo=" + roomInfo +
                ", bankerPlayerUid=" + bankerPlayerUid +
                ", bankerIndex=" + bankerIndex +
                ", curBureau=" + curBureau +
                ", gameing=" + gameing +
                '}';
    }
}
