package com.xiuxiu.app.protocol.api.temp.player;

public class GetUserInfo {
    public long uid;
    public String sign;     // md5(uid + key)

    @Override
    public String toString() {
        return "GetUserInfo{" +
                "uid=" + uid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
