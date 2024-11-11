package com.xiuxiu.app.protocol.client.room;

public class PCLIRoomNtfBeginBeforeInfo {
    public int beginRemain = -1;            // 开始剩余时间(s). -1: 不自动开始

    @Override
    public String toString() {
        return "PCLIRoomNtfBeginBeforeInfo{" +
                "beginRemain=" + beginRemain +
                '}';
    }
}
