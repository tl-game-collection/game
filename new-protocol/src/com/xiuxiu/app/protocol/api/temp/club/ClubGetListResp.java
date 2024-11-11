package com.xiuxiu.app.protocol.api.temp.club;

import com.xiuxiu.core.net.protocol.ErrorMsg;

import java.util.ArrayList;
import java.util.List;

public class ClubGetListResp extends ErrorMsg {
    public static class ClubInfo {
        public long uid;
        public String name;
        public int type; //俱乐部类型1房卡2金币
        public String desc;
        public String gameDesc;
        public long ownerUid;
        public String ownerName;
        public int clubSize;//本圈人数
        public float roomCards;//消耗房卡数
        public int gold;//金币数
        public int reward;//奖励分
        public int fuseCount;//合圈数量
        public int fuseClubSize;//总圈人数
        public float fuseClubCards;//总圈消耗房卡数
        public long createTime;
        public boolean canSetTreasureInfo;
        public float ownerRoomCards;

        @Override
        public String toString() {
            return "ClubInfo{" +
                    "uid=" + uid +
                    ", name='" + name + '\'' +
                    ", type=" + type +
                    ", desc='" + desc + '\'' +
                    ", gameDesc='" + gameDesc + '\'' +
                    ", ownerUid=" + ownerUid +
                    ", ownerName='" + ownerName + '\'' +
                    ", clubSize=" + clubSize +
                    ", roomCards=" + roomCards +
                    ", gold=" + gold +
                    ", reward=" + reward +
                    ", fuseCount=" + fuseCount +
                    ", fuseClubSize=" + fuseClubSize +
                    ", fuseClubCards=" + fuseClubCards +
                    ", createTime=" + createTime +
                    ", canSetTreasureInfo=" + canSetTreasureInfo +
                    '}';
        }
    }

    public int page;
    public int pageSize;
    public List<ClubInfo> list = new ArrayList<>();
    public int totalSize;

    @Override
    public String toString() {
        return "ClubGetListResp{" +
                "page=" + page +
                ", pageSize=" + pageSize +
                ", list=" + list +
                ", totalSize=" + totalSize +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                '}';
    }
}
