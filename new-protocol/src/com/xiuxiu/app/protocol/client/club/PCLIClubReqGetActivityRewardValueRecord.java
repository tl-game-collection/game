package com.xiuxiu.app.protocol.client.club;

/**
 *
 */
public class PCLIClubReqGetActivityRewardValueRecord {
    public long clubUid;            // 俱乐部uid
    public int type;                // 模式, 1.游戏玩法总领取 2.活动周期领取情况；3.周期内玩家领取详情
    public int page;                // 分页, 从0开始
    public long boxUid;             // 牌桌Uid
    public long startTime;          // 开始时间
    public long endTime;            // 结束时间


    @Override
    public String toString() {
        return "PCLIClubReqGetActivityRewardValueRecord{" +
                "clubUid=" + clubUid +
                ", type=" + type +
                ", page=" + page +
                ", boxUid=" + boxUid +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
