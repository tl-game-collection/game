package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class ApiUserLoginInfoResp extends ErrorMsg {
    public static class ApiUserInfo {
        public String token;

        @Override
        public String toString() {
            return "ApiUserInfo{" +
                    "token='" + token + '\'' +
                    '}';
        }
    }

    public ApiUserInfo data;

    @Override
    public String toString() {
        return "ApiUserLoginInfoResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
