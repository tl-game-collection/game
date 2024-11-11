package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class KillArenaRoomResp extends ErrorMsg {

    @Override
    public String toString() {
        return "KillArenaRoomResp{" +
                "ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
