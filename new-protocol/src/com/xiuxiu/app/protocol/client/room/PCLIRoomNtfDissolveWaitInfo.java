package com.xiuxiu.app.protocol.client.room;

import java.util.ArrayList;
import java.util.List;

public class PCLIRoomNtfDissolveWaitInfo {
    public int roomId;
    public List<Long> agreePlayerUid = new ArrayList<>();
    public int remain = 0;                                  // 剩余秒数

    @Override
    public String toString() {
        return "PCLIRoomNtfDissolveWaitInfo{" +
                "roomId=" + roomId +
                ", agreePlayerUid=" + agreePlayerUid +
                ", remain=" + remain +
                '}';
    }
}
