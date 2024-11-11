package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class DailyShareAwardResp extends ErrorMsg {
    public long groupUid;
    public long playerUid;
    public int arenaValue;

    @Override
    public String toString() {
        return "DailyShareAwardResp{" +
                "groupUid=" + groupUid +
                ", playerUid=" + playerUid +
                ", arenaValue=" + arenaValue +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
