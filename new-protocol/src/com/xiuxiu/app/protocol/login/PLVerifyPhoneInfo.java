package com.xiuxiu.app.protocol.login;

public class PLVerifyPhoneInfo {
    public String phone;        // 手机号
    public String authCode;     // 验证码
    public long accountUid;     // 账号uid

    @Override
    public String toString() {
        return "PLVerifyPhoneInfo{" +
                "phone='" + phone + '\'' +
                ", authCode='" + authCode + '\'' +
                ", accountUid=" + accountUid +
                '}';
    }
}
