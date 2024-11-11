package com.xiuxiu.app.protocol.api.temp.player;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class GetUserInfoResp extends ErrorMsg {
    public static class UserInfo {
        public long uid;
        public String nickName;
        public String avatar;
        public String phone;
        public String bankCard;
        public int diamond;
        public int wallet;
        public int league;
        public long recommendedPlayerUid;             // 推荐人ID
        public long createAt;
        public String sign;         // md5(uid + nickName + avatar + phone + bankCard + diamond + wallet + league + recommendedPlayerUid + createAt + key)

        @Override
        public String toString() {
            return "UserInfo{" +
                    "uid=" + uid +
                    ", nickName='" + nickName + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", phone='" + phone + '\'' +
                    ", bankCard='" + bankCard + '\'' +
                    ", diamond=" + diamond +
                    ", wallet=" + wallet +
                    ", league=" + league +
                    ", recommendedPlayerUid=" + recommendedPlayerUid +
                    ", createAt=" + createAt +
                    ", sign='" + sign + '\'' +
                    '}';
        }
    }

    public UserInfo data;

    @Override
    public String toString() {
        return "GetUserInfoResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
