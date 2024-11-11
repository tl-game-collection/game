package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

public class GetMyGroupsInfoResp extends ErrorMsg {
    public static class GroupInfo {
        public long groupUid;
        public String groupName;
        public String groupAvatar;
        public int totalCost;
        public String sign;         // md5(groupUid + groupName + groupAvatar + totalCost + key)

        @Override
        public String toString() {
            return "GroupInfo{" +
                    "groupUid=" + groupUid +
                    ", groupName='" + groupName + '\'' +
                    ", groupAvatar='" + groupAvatar + '\'' +
                    ", totalCost=" + totalCost +
                    ", sign='" + sign + '\'' +
                    '}';
        }
    }

    public List<GroupInfo> data = new ArrayList<>();

    @Override
    public String toString() {
        return "GetMyGroupsInfoResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
