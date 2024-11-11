package com.xiuxiu.app.server.statistics;

import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.app.server.db.UIDManager;
import com.xiuxiu.app.server.db.UIDType;
import com.xiuxiu.core.utils.TimeUtil;

public class TodayStatistics extends BaseTable {
    protected long playerUid;
    protected long fromUid;
    protected int statisticsType;
    protected int value;
    protected long updateTime;

    public TodayStatistics() {
        this.tableType = ETableType.TB_TODAY_STATISTICS;
    }

    public long getPlayerUid() {
        return playerUid;
    }

    public void setPlayerUid(long playerUid) {
        this.playerUid = playerUid;
    }

    public long getFromUid() {
        return fromUid;
    }

    public void setFromUid(long fromUid) {
        this.fromUid = fromUid;
    }

    public int getStatisticsType() {
        return statisticsType;
    }

    public void setStatisticsType(int statisticsType) {
        this.statisticsType = statisticsType;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void addValue(int value,long nowTime){
        if (TimeUtil.isSameDay(nowTime,this.getUpdateTime())){
            this.setValue(this.getValue() + value);
        }else{
            this.setValue(value);
            this.setUpdateTime(nowTime);
        }
        this.dirty = true;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public static TodayStatistics create(long playerUid, long fromUid, int statisticsType){
        TodayStatistics item = new TodayStatistics();
        item.setUid(UIDManager.I.getAndInc(UIDType.TODAY_STATISTICS));
        item.setPlayerUid(playerUid);
        item.setFromUid(fromUid);
        item.setStatisticsType(statisticsType);
        return item;
    }

    @Override
    public String toString() {
        return "TodayStatistics{" +
                "uid=" + uid +
                ", playerUid=" + playerUid +
                ", fromUid=" + fromUid +
                ", statisticsType=" + statisticsType +
                ", value=" + value +
                ", updateTime=" + updateTime +
                '}';
    }
}
