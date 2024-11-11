package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomNtfKillInfo {
    public int roomId;
    public int state;               // 1: 被踢, 2: 解散

    @Override
    public String toString() {
        return "PCLIRoomNtfKillInfo{" +
                "roomId=" + roomId +
                ", state=" + state +
                '}';
    }
}
