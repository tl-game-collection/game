package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class AddUserArenaResp extends ErrorMsg {
    public static class ArenaInfo {
        public long groupUid;
        public long userUid;
        public int currentArenaValue;
        public String sign;         // md5(groupUid + userUid + currentArenaValue + key)

        @Override
        public String toString() {
            return "ArenaInfo{" +
                    "groupUid=" + groupUid +
                    ", userUid=" + userUid +
                    ", currentDiamond=" + currentArenaValue +
                    ", sign='" + sign + '\'' +
                    '}';
        }
    }

    public ArenaInfo data;

    @Override
    public String toString() {
        return "AddUserDiamondResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
