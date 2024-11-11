package com.xiuxiu.app.protocol.api;

public class GetMyGroupCostSum {
    public long uid;
    public String sign; // md5(uid + key)

    @Override
    public String toString() {
        return "GetMyGroupCostSum{" +
                "uid=" + uid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
