package com.xiuxiu.app.protocol.client.hundred;

public class PCLIHundredReqSelfBankerList {
    public long boxId;
    public long roomId;
    public int page;
    public int pageSize;

    @Override
    public String toString() {
        return "PCLIHundredReqSelfBankerList{" +
                "boxId=" + boxId +
                ", roomId=" + roomId +
                ", page=" + page +
                ", pageSize=" + pageSize +
                '}';
    }
}
