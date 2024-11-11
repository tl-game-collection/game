package com.xiuxiu.app.protocol.api;

public class GetGroupInfoByArenaUid {
    public long gid;
    public long uid;
    public String sign; // md5(gid + uid + key)

    @Override
    public String toString() {
        return "AddUserDiamond{" +
                "gid=" + gid +
                ", uid=" + uid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
