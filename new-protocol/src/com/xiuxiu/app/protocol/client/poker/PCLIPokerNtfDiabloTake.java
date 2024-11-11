package com.xiuxiu.app.protocol.client.poker;

public class PCLIPokerNtfDiabloTake {
    public long PlayerUid;          //出牌完成的玩家

    @Override
    public String toString() {
        return "PCLIPokerNtfDiabloTake{" +
                "PlayerUid=" + PlayerUid +
                '}';
    }
}
