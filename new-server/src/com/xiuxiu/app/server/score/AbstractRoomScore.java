package com.xiuxiu.app.server.score;

import com.xiuxiu.app.server.db.BaseTable;

public abstract class AbstractRoomScore extends BaseTable implements IRoomScore {
    protected long roomUid;
    protected int roomId;
    protected int gameType;
    protected int gameSubType;
    protected int roomType;
    protected long groupUid;
    protected long beginTime;
    protected long endTime;

    public long getRoomUid() {
        return roomUid;
    }

    @Override
    public void setRoomUid(long roomUid) {
        this.roomUid = roomUid;
    }

    public int getRoomId() {
        return roomId;
    }

    @Override
    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getGameType() {
        return gameType;
    }

    @Override
    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public int getGameSubType() {
        return gameSubType;
    }

    @Override
    public void setGameSubType(int gameSubType) {
        this.gameSubType = gameSubType;
    }

    public int getRoomType() {
        return roomType;
    }

    @Override
    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }

    public long getGroupUid() {
        return groupUid;
    }

    @Override
    public void setGroupUid(long groupUid) {
        this.groupUid = groupUid;
    }

    public long getBeginTime() {
        return beginTime;
    }

    @Override
    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    @Override
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
