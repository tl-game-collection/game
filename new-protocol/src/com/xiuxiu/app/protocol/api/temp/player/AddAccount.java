package com.xiuxiu.app.protocol.api.temp.player;

/**
 *
 */
public class AddAccount {
    public long uid;
    public String phone;
    public String sign;         // md5(uid + phone + key)

    @Override
    public String toString() {
        return "AddAccount{" +
                "uid=" + uid +
                ", phone='" + phone + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
