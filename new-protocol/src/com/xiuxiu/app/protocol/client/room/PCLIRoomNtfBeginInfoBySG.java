package com.xiuxiu.app.protocol.client.room;

import java.util.HashMap;

public class PCLIRoomNtfBeginInfoBySG extends PCLIRoomNtfBeginInfo {
    public HashMap<Long, Integer> pushNoteScore = new HashMap<>();  // 所有文件推注倍数

    @Override
    public String toString() {
        return "PCLIRoomNtfBeginInfoBySG{" +
                "pushNoteScore=" + pushNoteScore +
                ", bankerIndex=" + bankerIndex +
                ", bureau=" + bureau +
                ", roomBriefInfo=" + roomBriefInfo +
                ", d=" + d +
                '}';
    }
}
