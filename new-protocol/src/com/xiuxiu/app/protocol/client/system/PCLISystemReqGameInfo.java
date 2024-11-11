package com.xiuxiu.app.protocol.client.system;

public class PCLISystemReqGameInfo {
    public int containerType; // 1-房间，2-竞技场，3-包厢
    public int containerUid;
    public long groupUid; // 群UID，仅在containerType为3时有意义

    @Override
    public String toString() {
        return "PCLISystemReqGameInfo{" +
                "containerType=" + containerType +
                ", containerUid=" + containerUid +
                ", groupUid=" + groupUid +
                '}';
    }
}
