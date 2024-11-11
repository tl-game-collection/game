package com.xiuxiu.app.protocol.api;

/**
 *
 */
public class MoneyExpendRecordList {
    public int page;
    public int pageSize;
    public int roomType;
    public long playerUid;
    public String sign; // med(page, pageSize, key)

    @Override
    public String toString() {
        return "MoneyExpendRecordList{" +
                "page=" + page +
                ", pageSize=" + pageSize +
                ", roomType=" + roomType +
                ", playerUid=" + playerUid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
