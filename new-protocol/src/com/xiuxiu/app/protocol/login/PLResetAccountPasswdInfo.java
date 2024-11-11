package com.xiuxiu.app.protocol.login;

public class PLResetAccountPasswdInfo {
    public String phone;        // 手机号
    public String authCode;     // 验证码
    public String newPasswd;    // 新密码
    public String sign;         // 签名 md5(phone + newPasswd + authCode + key)
    public long accountUid;     // 账号Uid

    @Override
    public String toString() {
        return "PLResetAccountPasswdInfo{" +
                "phone='" + phone + '\'' +
                ", authCode='" + authCode + '\'' +
                ", newPasswd='" + newPasswd + '\'' +
                ", sign='" + sign + '\'' +
                ", accountUid=" + accountUid +
                '}';
    }
}
