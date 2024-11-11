package com.xiuxiu.app.protocol.api.temp.club;

public class ChangeAccountPhone {
    public long playerUid;
    public long phoneNumber;
    public String sign;//playerUid + phoneNumber + key

    @Override
    public String toString() {
        return "ChangeAccountPhone{" +
                "playerUid=" + playerUid +
                ", phoneNumber=" + phoneNumber +
                ", sign='" + sign + '\'' +
                '}';
    }
}
