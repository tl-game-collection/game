package com.xiuxiu.app.protocol.api.temp.player;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class AddUserDiamondByDaTangResp extends ErrorMsg {
    public static class DiamondInfo {
        public long userUid;
        public int currentDiamond;
        public int type;                    // 类型 对应 EMoneyExpendType
        public long fromUid;                // 亲友圈ID 或者 联盟ID 没有为-1
        public long operatorUid;            // 被赠送的玩家id
        public String sign;         // md5(userUid + currentDiamond + key)

        @Override
        public String toString() {
            return "DiamondInfo{" +
                    "userUid=" + userUid +
                    ", currentDiamond=" + currentDiamond +
                    ", type=" + type +
                    ", fromUid=" + fromUid +
                    ", operatorUid=" + operatorUid +
                    ", sign='" + sign + '\'' +
                    '}';
        }
    }

    public DiamondInfo data;

    @Override
    public String toString() {
        return "AddUserDiamondByDaTangResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
