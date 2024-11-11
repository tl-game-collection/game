package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class GetHundredInfoResp extends ErrorMsg {
    public static class HundredInfo {
        public long groupUid;
        public long arenaUid;
        public int gameType;
        public int state;
        public int win;

        @Override
        public String toString() {
            return "HundredInfo{" +
                    "groupUid=" + groupUid +
                    ", arenaUid=" + arenaUid +
                    ", gameType=" + gameType +
                    ", state=" + state +
                    ", win=" + win +
                    '}';
        }
    }

    public HundredInfo data;

    @Override
    public String toString() {
        return "GetHundredInfoResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
