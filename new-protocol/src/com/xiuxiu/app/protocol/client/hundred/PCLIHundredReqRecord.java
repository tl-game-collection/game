package com.xiuxiu.app.protocol.client.hundred;

public class PCLIHundredReqRecord {
    public long boxId;
    public long roomId;
    public int page;
    public int pageSize;
    public boolean reb;         // 是否下注, true: 下注, false:局

    @Override
    public String toString() {
        return "PCLIHundredReqRecord{" +
                "boxId=" + boxId +
                ", roomId=" + roomId +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", reb=" + reb +
                '}';
    }
}
