package com.xiuxiu.app.server.statistics.moneyrecord;

import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;

public class MoneyExpendRecordDetail extends BaseTable {
    
    private static final long serialVersionUID = -1054162800885247022L;
    /** 亲友圈类型 */
    private int clubType;
    /** 亲友圈uid */
    private long clubUid;
    /** 玩家id */
    private long playerUid;
    /** 房间id */
    private long roomUid;
    /** 房卡数量 */
    private float value;
    /** 消耗类型 */
    private int type;
    /** 消耗时间 */
    private long time;
    
    private transient String playerName;
    private transient String showTime;

    public MoneyExpendRecordDetail() {
        this.tableType = ETableType.TB_MONEY_EXPEND_RECORD_DETAILS;
    }

    public int getClubType() {
        return clubType;
    }

    public void setClubType(int clubType) {
        this.clubType = clubType;
    }

    public long getClubUid() {
        return clubUid;
    }

    public void setClubUid(long clubUid) {
        this.clubUid = clubUid;
    }

    public long getPlayerUid() {
        return playerUid;
    }

    public void setPlayerUid(long playerUid) {
        this.playerUid = playerUid;
    }

    public long getRoomUid() {
        return roomUid;
    }

    public void setRoomUid(long roomUid) {
        this.roomUid = roomUid;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

}
