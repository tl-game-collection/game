package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class GetOnlineCountResp extends ErrorMsg {
    public static class OnlineInfo {
        public int onlineNum;           // 当前在线人数

        public OnlineInfo() {
        }

        @Override
        public String toString() {
            return "OnlineInfo{" +
                    "onlineNum=" + onlineNum +
                    '}';
        }
    }
    
    public OnlineInfo data = new OnlineInfo();

    @Override
    public String toString() {
        return "GetOnlineCountResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
