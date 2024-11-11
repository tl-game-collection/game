package com.xiuxiu.app.protocol.login;

public class PLChangeBindPhoneInfo {
    public String phone;        // 手机号
    public String authCode;     // 验证码
    public long accountUid;     // 账号uid
    public String oldPhone;     // 原手机号

    @Override
    public String toString() {
        return "PLChangeBindPhoneInfo{" +
                "phone='" + phone + '\'' +
                ", authCode='" + authCode + '\'' +
                ", accountUid=" + accountUid +
                ", oldPhone='" + oldPhone + '\'' +
                '}';
    }
}
