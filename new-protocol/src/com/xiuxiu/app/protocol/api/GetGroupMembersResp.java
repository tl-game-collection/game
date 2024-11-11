package com.xiuxiu.app.protocol.api;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

public class GetGroupMembersResp extends ErrorMsg {
    public static class GroupMember {
        public long playerUid;                                  // 成员Uid
        public long uplinePlayerUid;                            // 上级Id
        public List<Long> downlinePlayer = new ArrayList<>();   // 直属下级成员
        public int memberType;                                  // 职位
        public int channelId;                                   // 渠道ID

        @Override
        public String toString() {
            return "GroupMember{" +
                    "playerUid=" + playerUid +
                    ", uplinePlayerUid=" + uplinePlayerUid +
                    ", downlinePlayer=" + downlinePlayer +
                    ", memberType=" + memberType +
                    ", channelId=" + channelId +
                    '}';
        }
    }
    public GroupMember data;

    @Override
    public String toString() {
        return "GetGroupMembersResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
