package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class GetGroupInfoResp extends ErrorMsg {
    public static class GroupInfo {
        public long gid;
        public String groupName;
        public String groupIcon;
        public long groupOwner;
        public String groupOwnerName;
        public String sign;         // md5(gid + groupName + groupIcon + groupOwner + groupOwnerName + key)

        @Override
        public String toString() {
            return "GroupInfo{" +
                    "gid=" + gid +
                    ", groupName='" + groupName + '\'' +
                    ", groupIcon='" + groupIcon + '\'' +
                    ", groupOwner='" + groupOwner + '\'' +
                    ", groupOwnerName='" + groupOwnerName + '\'' +
                    ", sign='" + sign + '\'' +
                    '}';
        }
    }

    public GroupInfo data;

    @Override
    public String toString() {
        return "GetGroupInfoResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
