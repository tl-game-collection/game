package com.xiuxiu.app.protocol.client.club;

public class PCLIClubActivityGoldRewardRecordInfo {
    public long clubUid;               // 群uid
    public long boxUid;               // 包厢uid
    public int type;                    // 模式, 1: 加载每个竞技场任务领奖的竞技值, 2: 加载竞技场每一周期的列表, 3: 加载竞技场某一周期的详细列表
    public long time;                   // 时间, 时间戳(ms)
    public int page;                    // 分页, 从0开始
    public long startTime;              // 开始时间
    public long endTime;                // 结束时间

    @Override
    public String toString() {
        return "PCLIGroupReqLoadQuestArenaValueInfo{" +
                "clubUid=" + clubUid +
                ", boxUid=" + boxUid +
                ", type=" + type +
                ", time=" + time +
                ", page=" + page +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
