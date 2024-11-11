package com.xiuxiu.app.protocol.client.hundred;

public class PCLIHundredReqBankerList {
    public long boxId;
    public long roomId;
    public int page;

    @Override
    public String toString() {
        return "PCLIHundredReqBankerList{" +
                "boxId=" + boxId +
                ", roomId=" + roomId +
                ", page=" + page +
                '}';
    }
}
