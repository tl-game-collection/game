package com.xiuxiu.app.protocol.api;

public class AdminAddWallet {
    public long uid;
    public int amount;
    public String sign; // md5(uid + amount + type + key)

    @Override
    public String toString() {
        return "AdminAddWallet{" +
                "uid=" + uid +
                ", amount=" + amount +
                ", sign='" + sign + '\'' +
                '}';
    }
}
