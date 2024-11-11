package com.xiuxiu.app.protocol.api.temp.club;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

public class GetClubMemberRelationListResp extends ErrorMsg {
    public static class ClubMember {
        public long playerUid;                                  // 成员Uid
        public long uplinePlayerUid;                            // 上级Id
        public int memberType;                                  // 职位
        public String nickname;                                 // 昵称
        public String icon;                                     // 头像

        @Override
        public String toString() {
            return "ClubMember{" +
                    "playerUid=" + playerUid +
                    ", uplinePlayerUid=" + uplinePlayerUid +
                    ", memberType=" + memberType +
                    ", nickname='" + nickname + '\'' +
                    ", icon='" + icon + '\'' +
                    '}';
        }
    }
    public static class ClubInfo {
        public long clubUid;                                       // 俱乐部Uid
        public String clubName;                                    // 俱乐部名称
        public String clubIcon;                                    // 俱乐部头像
        public List<ClubMember> members = new ArrayList<>();       // 俱乐部成员列表

        @Override
        public String toString() {
            return "ClubInfo{" +
                    "clubUid=" + clubUid +
                    ", clubName='" + clubName + '\'' +
                    ", clubIcon='" + clubIcon + '\'' +
                    ", members=" + members +
                    '}';
        }
    }
    public ClubInfo data;

    @Override
    public String toString() {
        return "GetClubMemberRelationListResp{" +
                "data=" + data +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
