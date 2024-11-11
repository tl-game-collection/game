package com.xiuxiu.app.server.statistics.moneyrecord;

import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;

/**
 * 房卡消耗记录
 */
public class MoneyExpendRecord extends BaseTable {
    private long fromUid;       // fromId(亲友圈 或者 联盟)
    private long playerUid;     // 玩家id
    private long operatorUid;   // 操作人id
    private float value;          // 房卡数量
    private int expendType;     // 消耗类型
    private long expendTime;    // 消耗时间
    private int roomType;       // 消耗房间类型
    private int gameType;       // 游戏类型
    private long createTime;         // 当天0点时间

    public MoneyExpendRecord() {
        this.tableType = ETableType.TB_MONEY_EXPEND_RECORD;
    }

    public long getFromUid() {
        return fromUid;
    }

    public void setFromUid(long fromUid) {
        this.fromUid = fromUid;
    }

    public long getPlayerUid() {
        return playerUid;
    }

    public void setPlayerUid(long playerUid) {
        this.playerUid = playerUid;
    }

    public long getOperatorUid() {
        return operatorUid;
    }

    public void setOperatorUid(long operatorUid) {
        this.operatorUid = operatorUid;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public int getExpendType() {
        return expendType;
    }

    public void setExpendType(int expendType) {
        this.expendType = expendType;
    }

    public long getExpendTime() {
        return expendTime;
    }

    public void setExpendTime(long expendTime) {
        this.expendTime = expendTime;
    }

    public int getRoomType() {
        return roomType;
    }

    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }

    public int getGameType() {
        return gameType;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    @Override
    public String toString() {
        return "MoneyExpendRecord{" +
                "fromUid=" + fromUid +
                ", playerUid=" + playerUid +
                ", operatorUid=" + operatorUid +
                ", value=" + value +
                ", expendType=" + expendType +
                ", expendTime=" + expendTime +
                ", roomType=" + roomType +
                ", gameType=" + gameType +
                ", createTime=" + createTime +
                '}';
    }
}
