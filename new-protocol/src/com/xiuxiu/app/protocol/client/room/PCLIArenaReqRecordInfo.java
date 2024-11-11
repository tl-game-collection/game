package com.xiuxiu.app.protocol.client.room;

public class PCLIArenaReqRecordInfo {
    public int page;        // 从0开始
    public int gameType;                                                           // 游戏类型


    @Override
    public String toString() {
        return "PCLIArenaReqRecordInfo{" +
                ", page=" + page +
                ", gameType=" + gameType +
                '}';
    }
}
