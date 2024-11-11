package com.xiuxiu.app.server.room.record;

import java.util.HashMap;

public class RecordRoomBriefInfo {
    protected int roomId;
    protected int roomType;
    protected int gameType;
    protected int gameSubType;
    protected HashMap<String, Integer> rule;
    protected int bankerIndex;

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
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

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public int getGameSubType() {
        return gameSubType;
    }

    public void setGameSubType(int gameSubType) {
        this.gameSubType = gameSubType;
    }

    public HashMap<String, Integer> getRule() {
        return rule;
    }

    public void setRule(HashMap<String, Integer> rule) {
        this.rule = rule;
    }

    public int getBankerIndex() {
        return bankerIndex;
    }

    public void setBankerIndex(int bankerIndex) {
        this.bankerIndex = bankerIndex;
    }
}
