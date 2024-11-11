package com.xiuxiu.app.server.club.activity.gold;

import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;

public class ClubActivityGoldRewardRecord extends BaseTable {
    protected long clubUid; // 群ID
    protected long playerUid; // 用户ID
    protected String name; // 用户名称
    protected String icon; // 用户头像
    protected long boxUid; // 包厢ID
    protected int gold; // 领取的竞技值
    protected long operatorTime; // 领奖时间
    protected int gameType; // 游戏类型
    protected int subType; // 游戏子类型
    protected int bureau; // 局数
    protected long startTime; // 开始时间
    protected long endTime; // 结束时间
    protected long period; // 活动周期
    protected int param; // 活动条件

    public ClubActivityGoldRewardRecord() {
        this.tableType = ETableType.TB_CLUB_ACTIVITY_GOLD_REWARD_RECORD;
    }

    public long getPlayerUid() {
        return playerUid;
    }

    public void setPlayerUid(long playerUid) {
        this.playerUid = playerUid;
    }

    public long getClubUid() {
        return clubUid;
    }

    public void setClubUid(long clubUid) {
        this.clubUid = clubUid;
    }

    public long getBoxUid() {
        return boxUid;
    }

    public void setBoxUid(long boxUid) {
        this.boxUid = boxUid;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public long getOperatorTime() {
        return operatorTime;
    }

    public void setOperatorTime(long operatorTime) {
        this.operatorTime = operatorTime;
    }

    public int getGameType() {
        return gameType;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public int getSubType() {
        return subType;
    }

    public void setSubType(int subType) {
        this.subType = subType;
    }

    public int getBureau() {
        return bureau;
    }

    public void setBureau(int bureau) {
        this.bureau = bureau;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endrtTime) {
        this.endTime = endrtTime;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public int getParam() {
        return param;
    }

    public void setParam(int param) {
        this.param = param;
    }

    @Override
    public String toString() {
        return "QuestGetRewardRecord{" + "clubUid=" + clubUid + ", playerUid=" + playerUid + ", name=" + name
                + ", icon=" + icon + ", boxUid=" + boxUid + ", gold=" + gold + ", operatorTime="
                + operatorTime + ", gameType=" + gameType + ", subType=" + subType + ", bureau=" + bureau
                + ", startTime=" + startTime + ", endTime=" + endTime + ", period=" + period + ", param=" + param + '}';
    }

}
