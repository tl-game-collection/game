package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class PlayerWalletRechargeResp extends ErrorMsg {

    @Override
    public String toString() {
        return "PlayerWalletRechargeResp{" +
                "ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
