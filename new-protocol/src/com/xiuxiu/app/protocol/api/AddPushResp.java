package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class AddPushResp extends ErrorMsg {
    @Override
    public String toString() {
        return "AddPushResp{" +
                "ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
