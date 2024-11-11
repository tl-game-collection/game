package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.Arrays;

public class GetApiUserInfoResp extends ErrorMsg {
    public static class ApiUserInfo {
        public String[] roles;
        public String avatar;
        public String name;
        public String introduction;

        @Override
        public String toString() {
            return "ApiUserInfo{" +
                    "roles=" + Arrays.toString(roles) +
                    ", avatar='" + avatar + '\'' +
                    ", name='" + name + '\'' +
                    ", introduction='" + introduction + '\'' +
                    '}';
        }
    }

    public ApiUserInfo data;

    @Override
    public String toString() {
        return "GetApiUserInfoResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
