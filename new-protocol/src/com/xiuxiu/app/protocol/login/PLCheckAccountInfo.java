package com.xiuxiu.app.protocol.login;

public class PLCheckAccountInfo {
    public String phone;        // 手机号
    public String authCode;     // 验证码

    @Override
    public String toString() {
        return "PLCheckAccountInfo{" +
                "phone='" + phone + '\'' +
                ", authCode='" + authCode + '\'' +
                '}';
    }
}
