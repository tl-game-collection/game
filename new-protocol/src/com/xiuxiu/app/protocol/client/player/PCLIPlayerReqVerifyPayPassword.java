package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerReqVerifyPayPassword {
    public String payPassword;       // MD5(支付密码)

    @Override
    public String toString() {
        return "PCLIPlayerReqVerifyPayPassword{" +
                "payPassword='" + payPassword + '\'' +
                '}';
    }
}
