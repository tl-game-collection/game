package com.xiuxiu.app.server.statistics.consume;

import java.util.LinkedList;

public class PlayerMoneyConsumeMonthData {
    /**
     * 开始记录时间
     */
    private long startTime;
    /**
     * 最后更新时间
     */
    private long time;
    /**
     * 月统计数量
     */
    private LinkedList<Float> monthCount = new LinkedList<Float>();

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public LinkedList<Float> getMonthCount() {
        return monthCount;
    }

    public void setMonthCount(LinkedList<Float> monthCount) {
        this.monthCount = monthCount;
    }

}
