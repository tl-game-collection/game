package com.xiuxiu.app.protocol.api;

public class GetMyGroupCostDetailByGroupId {
    public long groupId = 0;
    public int limit;
    public int offset;
    public String sign; // md5(groupId + limit + offset + key)

    @Override
    public String toString() {
        return "GetMyGroupCostDetailByGroupId{" +
                "groupId=" + groupId +
                ", limit=" + limit +
                ", offset=" + offset +
                ", sign='" + sign + '\'' +
                '}';
    }
}
