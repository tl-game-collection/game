package com.xiuxiu.app.protocol.client.player;

public class PCLIPlayerReqChangePayPassWord {
    public String newPayPasswd;     // 新的支付密码

    @Override
    public String toString() {
        return "PCLIPlayerReqChangePayPassWord{" +
                "newPayPasswd='" + newPayPasswd  + '\'' +
                '}';
    }
}
