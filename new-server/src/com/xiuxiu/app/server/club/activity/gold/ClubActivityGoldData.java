package com.xiuxiu.app.server.club.activity.gold;

import java.util.ArrayList;
import java.util.List;

public class ClubActivityGoldData {

    /**
     * 竞技场ID
     */
    private List<ClubActivityGoldDataItem> items = new ArrayList<>();
    /**
     * 活动周期
     */
    private int period;
    /**
     * 活动开始时间
     */
    private long startTime;

    public List<ClubActivityGoldDataItem> getItems() {
        return items;
    }

    public void setItems(List<ClubActivityGoldDataItem> items) {
        this.items = items;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

}
