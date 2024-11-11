package com.xiuxiu.app.protocol.api;

public class AddUserDiamond {
    public long uid;
    public int amount;
    public int type;
    public String sign; // md5(uid + amount + type + key)

    @Override
    public String toString() {
        return "AddUserDiamond{" +
                "uid=" + uid +
                ", amount=" + amount +
                ", type=" + type +
                ", sign='" + sign + '\'' +
                '}';
    }
}
