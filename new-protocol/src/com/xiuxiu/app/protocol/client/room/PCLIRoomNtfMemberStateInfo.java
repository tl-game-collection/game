package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomNtfMemberStateInfo {
    public long playerUid;
    public int gameType;        // 游戏类型
    public int state;           // 0: 未准备, 1:准备

    @Override
    public String toString() {
        return "PCLIRoomNtfMemberStateInfo{" +
                "playerUid=" + playerUid +
                ", gameType=" + gameType +
                ", state=" + state +
                '}';
    }
}
