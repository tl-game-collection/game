package com.xiuxiu.app.protocol.client.system;

public class PCLISystemNtfGameInfo {
    public int containerType;
    public int containerUid;
    public long groupUid; // 群UID，仅在containerType为3时有意义
    public int gameType;
    public int gameSubType;

    @Override
    public String toString() {
        return "PCLISystemNtfGameInfo{" +
                "containerType=" + containerType +
                ", containerUid=" + containerUid +
                ", gameType=" + gameType +
                ", gameSubType=" + gameSubType +
                '}';
    }
}
