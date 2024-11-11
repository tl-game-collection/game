package com.xiuxiu.app.protocol.client.club;

import java.util.ArrayList;
import java.util.List;

public class PCLIClubReqActivityGoldModify {

    public long clubUid;                                                // 群ID
    public long boxUid;                                                // 竞技ID
    public List<PCLIClubActivityGold> items = new ArrayList<>();    // 任务列表
    public int period;                                                   // 活动周期
    public boolean reset;                                                // 是否重置修改周期重新开始
    @Override
    public String toString() {
        return "PCLIClubReqActivityGoldModify{" +
                "clubUid=" + clubUid +
                ", boxUid=" + boxUid +
                ", items=" + items +
                ", period=" + period +
                '}';
    }
}
