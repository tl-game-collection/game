package com.xiuxiu.app.protocol.api;

public class GetMyGroupCostSumByGroupId {
    public long uid;
    public long groupId = 0;
    public int limit;
    public int offset;
    public String sign; // md5(uid + groupId + limit + offset + key)

    @Override
    public String toString() {
        return "GetMyGroupCostSum{" +
                "uid=" + uid +
                ", groupId=" + groupId +
                ", limit=" + limit +
                ", offset=" + offset +
                ", sign='" + sign + '\'' +
                '}';
    }
}
