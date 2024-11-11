package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class GainToBoxScoreResp extends ErrorMsg {
    public long chiefPlayerUid = -1;

    @Override
    public String toString() {
        return "GainToBoxScoreResp{" +
                "chiefPlayerUid=" + chiefPlayerUid +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
