package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class SetPlayerPrivilegeInfoResp extends ErrorMsg {

    @Override
    public String toString() {
        return "SetPlayerPrivilegeInfoResp{" +
                "ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
