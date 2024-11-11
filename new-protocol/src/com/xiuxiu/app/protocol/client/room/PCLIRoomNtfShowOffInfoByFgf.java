package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomNtfShowOffInfoByFgf extends PCLIRoomNtfShowOffInfo {
    public int cardType;

    @Override
    public String toString() {
        return "PCLIRoomNtfShowOffInfoByFgf{" +
                "cardType=" + cardType +
                ", playerUid=" + playerUid +
                ", card=" + card +
                '}';
    }
}
