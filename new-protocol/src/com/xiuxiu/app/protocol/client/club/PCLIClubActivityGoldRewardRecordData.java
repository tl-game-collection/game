package com.xiuxiu.app.protocol.client.club;

import java.util.ArrayList;
import java.util.List;

public class PCLIClubActivityGoldRewardRecordData {
    public static  class ClubActivityGoldRewardRecordOne{
        public long clubUid;
        public String clubName;
        public int value;

        @Override
        public String toString() {
            return "ClubActivityGoldRewardRecordOne{" +
                    "clubUid=" + clubUid +
                    ", clubName='" + clubName + '\'' +
                    ", value=" + value +
                    '}';
        }
    }
    public static  class ClubActivityGoldRewardRecordTow{
        public long boxUid;
        public int gameType;
        public int gameSubType;
        public int value;

        @Override
        public String toString() {
            return "QuestArenaValueInfoTow{" +
                    "boxUid=" + boxUid +
                    ", gameType=" + gameType +
                    ", gameSubType=" + gameSubType +
                    ", value=" + value +
                    '}';
        }
    }

    public static  class ClubActivityGoldRewardRecordThree{
        public long StartTime;
        public long endTime;
        public int value;

        @Override
        public String toString() {
            return "QuestArenaValueInfoThree{" +
                    "StartTime=" + StartTime +
                    ", endTime=" + endTime +
                    ", value=" + value +
                    '}';
        }
    }
    public static  class ClubActivityGoldRewardRecordFour{
        public long playerUid;
        public String name;
        public String icon;
        public int value;
        public int bureau;

        @Override
        public String toString() {
            return "QuestArenaValueInfoFour{" +
                    "playerUid=" + playerUid +
                    ", name='" + name + '\'' +
                    ", icon='" + icon + '\'' +
                    ", value=" + value +
                    ", bureau=" + bureau +
                    '}';
        }
    }

    public long clubUid;          // 联盟ID
    public long boxUid;
    public int type;                // 模式, 1: 加载每个竞技场任务领奖的竞技值, 2: 加载竞技场每一周期的列表, 3: 加载竞技场某一周期的详细列表
    public int page;                // 分页, 从0开始
    public boolean next;            // 是否还有下一页
    public List<ClubActivityGoldRewardRecordOne> oneList = new ArrayList<>();        // type == 1 返回的
    public List<ClubActivityGoldRewardRecordTow> towList = new ArrayList<>();        // type == 2 返回的
    public List<ClubActivityGoldRewardRecordThree> threeList = new ArrayList<>();    // type == 3 返回的
    public List<ClubActivityGoldRewardRecordFour> fourList = new ArrayList<>();      // type == 4 返回的
    public int count;   // 总数

    @Override
    public String toString() {
        return "PCLIGroupNtfLoadQuestArenaValueRecordInfo{" +
                "clubUid=" + clubUid +
                ", type=" + type +
                ", page=" + page +
                ", next=" + next +
                ", oneList=" + oneList +
                ", towList=" + towList +
                ", threeList=" + threeList +
                ", fourList=" + fourList +
                ", count=" + count +
                '}';
    }
}
