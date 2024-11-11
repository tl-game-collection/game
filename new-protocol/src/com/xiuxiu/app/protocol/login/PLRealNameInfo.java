package com.xiuxiu.app.protocol.login;

public class PLRealNameInfo {
    public String name;        // 姓名
    public String idCard;     // 身份证
    public long accountUid;     // 账号uid

    @Override
    public String toString() {
        return "PLRealNameInfo{" +
                "name='" + name + '\'' +
                ", idCard='" + idCard + '\'' +
                ", accountUid=" + accountUid +
                '}';
    }
}
