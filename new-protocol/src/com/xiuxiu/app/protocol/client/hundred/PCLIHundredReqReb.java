package com.xiuxiu.app.protocol.client.hundred;

public class PCLIHundredReqReb {
    public long boxId;
    public int index;           // -1: 庄家
    public int value;           //
    public int type;            // 押注类型, 0: 闲家赢, 1: 庄家赢

    @Override
    public String toString() {
        return "PCLIHundredReqReb{" +
                "index=" + index +
                ", value=" + value +
                ", type=" + type +
                ", boxId=" + boxId +
                '}';
    }
}
