package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomNtfLeaveStateInfo {
    public int state;               // 1: 离开成功, 2: 暂时离开, 3: 需要解散才能离开, 4: 离开有战报

    @Override
    public String toString() {
        return "PCLIRoomNtfLeaveStateInfo{" +
                "state=" + state +
                '}';
    }
}
