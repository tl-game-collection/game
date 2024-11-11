package com.xiuxiu.app.protocol.client.hundred;

public class PCLIHundredReqVipSeatOp {
    public long boxId;
    public int index; // 坐下位置索引, -1: 站起

    @Override
    public String toString() {
        return "PCLIHundredReqVipSeatOp{" +
                "index=" + index +
                ", boxId=" + boxId +
                '}';
    }
}
