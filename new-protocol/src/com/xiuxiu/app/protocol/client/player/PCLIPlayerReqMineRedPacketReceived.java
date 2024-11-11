package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerReqMineRedPacketReceived {
    public long redPacketUid; // 红包uid

    @Override
    public String toString() {
        return "PCLIPlayerReqMineRedPacketReceived{" +
                "redPacketUid=" + redPacketUid +
                '}';
    }
}
