package com.xiuxiu.app.protocol.client.hundred;

public class PCLIHundredReqBankerRecord {
    public long boxId;
    public long roomId;
    public int page;

    @Override
    public String toString() {
        return "PCLIHundredReqBankerRecord{" +
                "boxId=" + boxId +
                ", roomId=" + roomId +
                ", page=" + page +
                '}';
    }
}
