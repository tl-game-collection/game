package com.xiuxiu.app.protocol.client.club.helper;

import java.util.ArrayList;
import java.util.List;

public class PCLIClubNtfHelperInvitePlayers {
    public long clubUid; // 亲友圈uid
    public int page; // 分页, 从0开始
    public boolean next; // 是否还有下一页
    public List<HelperInvitePlayersInfo> list = new ArrayList<PCLIClubNtfHelperInvitePlayers.HelperInvitePlayersInfo>();

    public static class HelperInvitePlayersInfo {
        public long id; // 玩家uid
        public String name; // 玩家name
        public String icon; // 玩家icon
    }

    @Override
    public String toString() {
        return "PCLIClubNtfHelperInvitePlayers{" + "clubUid=" + clubUid + '}';
    }
}
