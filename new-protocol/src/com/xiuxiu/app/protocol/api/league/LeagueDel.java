package com.xiuxiu.app.protocol.api.league;

public class LeagueDel {
    public long uid;
    public String sign; // md5(uid, key)

    @Override
    public String toString() {
        return "LeagueDel{" +
                "uid=" + uid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
