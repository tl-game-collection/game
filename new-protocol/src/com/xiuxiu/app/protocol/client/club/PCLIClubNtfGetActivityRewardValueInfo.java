package com.xiuxiu.app.protocol.client.club;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PCLIClubNtfGetActivityRewardValueInfo {
    public static class QuestArenaValueInfo {
        public long playerUid;          // 玩家uid
        public String playerName;       // 玩家name
        public String playerIcon;       // 玩家icon
        public long startTime;          // 开始时间
        public long endTime;            // 结束时间
        public long gold;               // 竞技分
        public long boxUid;             // 牌桌Uid
        public int gameType;            // 游戏类型
        public int gameSubType;         // 游戏子类型
        public int bureau;              // 总局数

        @Override
        public String toString() {
            return "QuestArenaValueInfo{" +
                    "playerUid=" + playerUid +
                    ", playerName='" + playerName + '\'' +
                    ", playerIcon='" + playerIcon + '\'' +
                    ", startTime=" + startTime +
                    ", endTime=" + endTime +
                    ", gold=" + gold +
                    ", boxUid=" + boxUid +
                    ", gameType=" + gameType +
                    ", gameSubType=" + gameSubType +
                    ", bureau=" + bureau +
                    '}';
        }
    }

    public long clubUid;            // 俱乐部uid
    public int type;                // 模式, 1.游戏玩法总领取 2.活动周期领取情况；3.周期内玩家领取详情
    public int page;                // 分页, 从0开始
    public boolean next;            // 是否还有下一页
    public long count;
    public List<QuestArenaValueInfo> data = new ArrayList<>(); // 数据

    @Override
    public String toString() {
        return "PCLIClubNtfGetActivityRewardValueInfo{" +
                "clubUid=" + clubUid +
                ", type=" + type +
                ", page=" + page +
                ", next=" + next +
                ", data=" + data +
                ", count=" + count +
                '}';
    }
}
