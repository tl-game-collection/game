package com.xiuxiu.app.protocol.api.temp.club;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

public class GetClubMemberListResp extends ErrorMsg {
    public static class PlayerListInfo {
        public long uid;
        public String name;
        public String icon;
        public long gold;//金币数
        public long reward;//奖励分
        public long clubOwnerId;
        public String clubName;//所属群
        
        

        @Override
        public String toString() {
            return "playerList{" +
                    "uid=" + uid +
                    ", name='" + name + '\'' +
                    ", icon='" + icon + '\'' +
                    '}';
        }
    }

    public long playerUid;
    public long clubUid;
    public int page;
    public int pageSize;
    public int totalSize;
    public boolean next;
    public List<PlayerListInfo> list = new ArrayList<>();

    @Override
    public String toString() {
        return "GetClubMemberListResp{" +
                "playerUid=" + playerUid +
                ", clubUid=" + clubUid +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", next=" + next +
                ", list=" + list +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
