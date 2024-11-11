package com.xiuxiu.app.server.club.impl;

import com.xiuxiu.app.server.db.BaseTable;

/**
 * @author xx
 */
public abstract class AbstractClubValueRecord extends BaseTable {
    private long playerUid;           // 玩家Uid
    private int action;               // 操作类型
    private long inMoney;             // 收入金额
    private long outMoney;            // 支出金额
    private long beginAmount;         // 操作前的金额
    private long optPlayerUid;         // 操作人Uid
    private long mainClubUid;          // 主圈uid
    private long clubUid;             // 圈uid
    private long createdAt;           // 操作时间（当天零点，方便读取）
    private long optTime;             // 具体操作时间
    private long mount;               // 改变量

    public long getPlayerUid() {
        return playerUid;
    }

    public void setPlayerUid(long playerUid) {
        this.playerUid = playerUid;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public long getInMoney() {
        return inMoney;
    }

    public void setInMoney(long inMoney) {
        this.inMoney = inMoney;
    }

    public long getOutMoney() {
        return outMoney;
    }

    public void setOutMoney(long outMoney) {
        this.outMoney = outMoney;
    }

    public long getBeginAmount() {
        return beginAmount;
    }

    public void setBeginAmount(long beginAmount) {
        this.beginAmount = beginAmount;
    }

    public long getOptPlayerUid() {
        return optPlayerUid;
    }

    public void setOptPlayerUid(long optPlayerUid) {
        this.optPlayerUid = optPlayerUid;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getOptTime() {
        return optTime;
    }

    public void setOptTime(long optTime) {
        this.optTime = optTime;
    }

    public long getMainClubUid() {
        return mainClubUid;
    }

    public void setMainClubUid(long mainClubUid) {
        this.mainClubUid = mainClubUid;
    }

    public long getClubUid() {
        return clubUid;
    }

    public void setClubUid(long clubUid) {
        this.clubUid = clubUid;
    }

    public long getMount() {
        return mount;
    }

    public void setMount(long mount) {
        this.mount = mount;
    }
}
