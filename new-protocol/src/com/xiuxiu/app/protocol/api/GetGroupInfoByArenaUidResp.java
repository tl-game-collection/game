package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

public class GetGroupInfoByArenaUidResp extends ErrorMsg {
    public static class GroupInfo {
        public long gid;
        public long uid;
        public String groupName;
        public long groupOwner;
        public String groupOwnerName;
        public String sign;         // md5(gid + uid + groupName + groupOwner + groupOwnerName + key)

        @Override
        public String toString() {
            return "GroupInfo{" +
                    "gid=" + gid +
                    ", uid='" + uid + '\'' +
                    ", groupName='" + groupName + '\'' +
                    ", groupOwner='" + groupOwner + '\'' +
                    ", groupOwnerName='" + groupOwnerName + '\'' +
                    ", sign='" + sign + '\'' +
                    '}';
        }
    }

    public GroupInfo data;

    @Override
    public String toString() {
        return "GetGroupInfoByGidResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
