package com.xiuxiu.app.protocol.client.club;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PCLIClubNtfGetMemberList {
    public static class memberList {
        public long playerUid;                   //playerUid
        public String icon;
        public String name;
        public long joinTime;
        public int jobType;                      //职位
        public long privilege;                   //权限
        public int showNick;                     //显示昵称
        public long offlineTime;                 //离线时间
        public long uplinePlayerUid;             //上线uid
        public int state;                        //成员状态0.正常 1.禁玩
        public long score;                       //竞技分
        public int divide;                       //奖励分分成比例活动，针对成员
        public int divideLine;                   //奖励分分成比例活动，针对一条线
        public int onlyUpLineSetGold;             //是否只有上级才能上下分
        public long code;   //推荐码
        public boolean isUpGoldTreasurer;       //是否是上分财务
        public boolean isDownGoldTreasurer;       //是否是下分财务

        @Override
        public String toString() {
            return "memberList{" +
                    "playerUid=" + playerUid +
                    ", icon='" + icon + '\'' +
                    ", name='" + name + '\'' +
                    ", joinTime=" + joinTime +
                    ", jobType=" + jobType +
                    ", privilege=" + privilege +
                    ", showNick=" + showNick +
                    ", offlineTime=" + offlineTime +
                    ", uplinePlayerUid=" + uplinePlayerUid +
                    ", state=" + state +
                    ", score=" + score +
                    ", divide=" + divide +
                    ", divideLine=" + divideLine +
                    ", onlyUpLineSetGold=" + onlyUpLineSetGold +
                    ", code=" + code +
                    ", isUpGoldTreasurer=" + isUpGoldTreasurer +
                    ", isDownGoldTreasurer=" + isDownGoldTreasurer +
                    '}';
        }
    }

    public long clubUid;
    public int page;
    public int pageSize;
    public boolean next;
    public boolean onlyDownLine; //只看下级
    public List<memberList> lists = new ArrayList<>();
    public int allClubPlayerCount;

    @Override
    public String toString() {
        return "PCLIClubNtfGetMemberList{" +
                "clubUid=" + clubUid +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", next=" + next +
                ", onlyDownLine=" + onlyDownLine +
                ", lists=" + lists +
                ", allClubPlayerCount=" + allClubPlayerCount +
                '}';
    }
}
