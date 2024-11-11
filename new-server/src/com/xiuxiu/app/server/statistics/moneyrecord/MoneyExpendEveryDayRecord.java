package com.xiuxiu.app.server.statistics.moneyrecord;

/**
 *  每天 每个渠道的房卡消耗总合
 */
public class MoneyExpendEveryDayRecord {
    private int roomType;           // 房卡消耗渠道类型
    private int count;              // 数量
    private String time;            // 时间
    private long createTime;          // 当天零点时间
    private int gameType;           // 游戏类型

    public int getRoomType() {
        return roomType;
    }

    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getGameType() {
        return gameType;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    @Override
    public String toString() {
        return "MoneyExpendEveryDayRecord{" +
                "roomType=" + roomType +
                ", count=" + count +
                ", time='" + time + '\'' +
                ", createTime=" + createTime +
                ", gameType=" + gameType +
                '}';
    }
}
