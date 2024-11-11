package com.xiuxiu.app.protocol.api.temp.trade;

public class AddOrDelTreasurerInfo {
    public long clubUid;
    public long playerUid;
    public boolean isSet;
    public String descOne; //支付宝,微信
    public String descTwo; //其他描述
    public String sign;// md5(clubUid + playerUid + key)

    @Override
    public String toString() {
        return "AddOrDelTreasurerInfo{" +
                "clubUid=" + clubUid +
                ", playerUid=" + playerUid +
                ", isSet=" + isSet +
                ", descOne='" + descOne + '\'' +
                ", descTwo='" + descTwo + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
