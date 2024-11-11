package com.xiuxiu.app.protocol.client.box;

public class PCLIBoxNtfChangeStateInfo {
    public PCLIBoxRoomStateInfo stateInfo = new PCLIBoxRoomStateInfo();
    public long groupUid;
    public long boxUid;

    @Override
    public String toString() {
        return "PCLIBoxNtfChangeStateInfo{" +
                "stateInfo=" + stateInfo +
                ", groupUid=" + groupUid +
                ", boxUid=" + boxUid +
                '}';
    }
}
