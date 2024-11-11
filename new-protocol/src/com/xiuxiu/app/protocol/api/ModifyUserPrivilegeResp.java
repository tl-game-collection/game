package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class ModifyUserPrivilegeResp extends ErrorMsg {
    @Override
    public String toString() {
        return "ModifyUserPrivilegeResp{" +
                "ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
