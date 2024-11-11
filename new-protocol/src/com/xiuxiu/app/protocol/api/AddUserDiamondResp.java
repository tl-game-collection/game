package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class AddUserDiamondResp extends ErrorMsg {
    public static class DiamondInfo {
        public long userUid;
        public int currentDiamond;
        public String sign;         // md5(userUid + currentDiamond + key)

        @Override
        public String toString() {
            return "DiamondInfo{" +
                    "userUid=" + userUid +
                    ", currentDiamond=" + currentDiamond +
                    ", sign='" + sign + '\'' +
                    '}';
        }
    }

    public DiamondInfo data;

    @Override
    public String toString() {
        return "AddUserDiamondResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
