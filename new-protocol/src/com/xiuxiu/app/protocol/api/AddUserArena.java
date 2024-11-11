package com.xiuxiu.app.protocol.api;

public class AddUserArena {
    public long gid;
    public long uid;
    public int arenaValue;
    public int payType;     // 支付类型, 1: 支付宝, 2: 微信, 3: 银联
    public String sign; // md5(gid + uid + arenaValue + payType + key)

    @Override
    public String toString() {
        return "AddUserArena{" +
                "gid=" + gid +
                ", uid=" + uid +
                ", arenaValue=" + arenaValue +
                ", payType=" + payType +
                ", sign='" + sign + '\'' +
                '}';
    }
}
