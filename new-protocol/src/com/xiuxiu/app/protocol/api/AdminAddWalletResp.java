package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class AdminAddWalletResp extends ErrorMsg {
    public static class Info {
        public long userUid;
        public int current;
        public String sign;         // md5(userUid + current + key)

        @Override
        public String toString() {
            return "Info{" +
                    "userUid=" + userUid +
                    ", current=" + current +
                    ", sign='" + sign + '\'' +
                    '}';
        }
    }

    public Info data;

    @Override
    public String toString() {
        return "AdminAddWalletResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
