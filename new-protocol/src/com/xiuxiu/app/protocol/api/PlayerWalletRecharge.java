package com.xiuxiu.app.protocol.api;

public class PlayerWalletRecharge {
    public long uid = 0;
    public int total;
    public String sign;    //md5(uid + total)

    @Override
    public String toString() {
        return "PlayerWalletRecharge{" +
                "uid=" + uid +
                ", total=" + total +
                ", sign='" + sign + '\'' +
                '}';
    }
}
