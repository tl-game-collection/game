package com.xiuxiu.app.protocol.api;

public class GetMyGroupsInfo {
    public long uid;
    public String gname;
    public String sign;         // md5(uid + gname + key)

    @Override
    public String toString() {
        return "GetMyGroupsInfo{" +
                "uid=" + uid +
                ", gname='" + gname + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
