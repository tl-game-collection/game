package com.xiuxiu.app.protocol.api;

public class GetGroupInfo {
    public long gid;
    public String sign; // md5(gid + key)

    @Override
    public String toString() {
        return "GetGroupInfo{" +
                "gid=" + gid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
