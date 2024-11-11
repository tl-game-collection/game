package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class GetPlayerMoneyExpendRecordResp extends ErrorMsg {
    public float count;

    @Override
    public String toString() {
        return "GetPlayerMoneyExpendRecordResp{" +
                "count=" + count +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
