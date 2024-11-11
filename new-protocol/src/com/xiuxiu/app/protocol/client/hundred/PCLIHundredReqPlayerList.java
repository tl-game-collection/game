package com.xiuxiu.app.protocol.client.hundred;

public class PCLIHundredReqPlayerList {
    public long boxId;
    public long roomId;
    public int page;

    @Override
    public String toString() {
        return "PCLIHundredReqPlayerList{" +
                "boxId=" + boxId +
                ", roomId=" + roomId +
                ", page=" + page +
                '}';
    }
}
