package com.xiuxiu.app.protocol.client.club;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PCLIClubNtfGetRewardValueRecordInfo {
    public long clubUid;                        // 俱乐部Uid
    public List<DivideAdminInfo> adminInfos = new ArrayList<>();
    public int type;                // 模式, 1: 加载游戏奖励分, 2: 加载每一天的列表, 3: 加载某一天的详细列表
    public int page;                // 分页, 从0开始
    public boolean next;            // 是否还有下一页
    public long awardScore;          // 奖励分
    public int managementCost;      // 管理费

    @Override
    public String toString() {
        return "PCLIClubNtfGetRewardValueRecordInfo{" +
                "clubUid=" + clubUid +
                ", adminInfos=" + adminInfos +
                ", awardScore=" + awardScore +
                ", managementCost=" + managementCost +
                ", type=" + type +
                ", page=" + page +
                ", next=" + next +
                '}';
    }

    public static class DivideAdminInfo{
        public long palyerUid;
        public String name;
        public String icon;
        public long value;
        public long time;

        @Override
        public String toString() {
            return "DivideAdminInfo{" +
                    "palyerUid=" + palyerUid +
                    ", name='" + name + '\'' +
                    ", icon='" + icon + '\'' +
                    ", value=" + value +
                    ", time=" + time +
                    '}';
        }
    }
}
