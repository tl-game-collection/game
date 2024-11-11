package com.xiuxiu.app.protocol.api.temp.trade;

public class ChangeTreasurerInfo {
    public long clubUid;
    public long playerUid;
    public String descOne; //支付宝,微信
    public String descTwo; //其他描述
    public String sign;// md5(clubUid + playerUid + key)

    @Override
    public String toString() {
        return "ChangeTreasurerInfo{" +
                "clubUid=" + clubUid +
                ", playerUid=" + playerUid +
                ", descOne='" + descOne + '\'' +
                ", descTwo='" + descTwo + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
