package com.xiuxiu.app.protocol.api.temp.player;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

public class GetClubMemberDownLineResp extends ErrorMsg {
    public static class MemberInfo {
        public long playerUid;
        public String avatar;
        public String nickName;
        public long upLinePlayerUid;

        @Override
        public String toString() {
            return "MemberInfo{" +
                    "playerUid=" + playerUid +
                    ", avatar='" + avatar + '\'' +
                    ", nickName='" + nickName + '\'' +
                    ", upLinePlayerUid=" + upLinePlayerUid +
                    '}';
        }
    }

    public List<MemberInfo> data = new ArrayList<>();

    @Override
    public String toString() {
        return "GetGroupInfoByGidResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
