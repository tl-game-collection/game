package com.xiuxiu.app.protocol.login;

public class PLAccountTransferDataInfo {
    public int type;            // 3: 微信登陆
    public String token;  // 微信，老token
    public int newChannel;      // 微信，新渠道号
    public String newAuthCode;  // 微信，新验证码

    @Override
    public String toString() {
        return "PLAccountTransferDataInfo{" +
                "type=" + type +
                ", token='" + token + '\'' +
                ", newChannel=" + newChannel +
                ", newAuthCode='" + newAuthCode + '\'' +
                '}';
    }
}
