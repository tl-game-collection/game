package com.xiuxiu.app.server.room.normal;

import com.alibaba.fastjson.TypeReference;
import com.xiuxiu.core.BaseObject;
import com.xiuxiu.core.utils.JsonUtil;

import java.util.HashMap;

public class RoomInfo extends BaseObject {
    protected int roomId;
    protected long ownerPlayerUid;
    protected long groupUid;
    protected long createTime;
    protected long endTime;
    protected int state;            // 0:初始化, 1:进行中, 2:完成
    protected int gameType;         // 游戏类型
    protected int gameSubType;      // 游戏子类型
    protected HashMap<String, Integer> rule = new HashMap<>();
    protected int cost;
    protected int curBureau;
    protected int ownerType;        // 房间所属
    protected String remarks;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public long getOwnerPlayerUid() {
        return ownerPlayerUid;
    }

    public void setOwnerPlayerUid(long ownerPlayerUid) {
        this.ownerPlayerUid = ownerPlayerUid;
    }

    public long getGroupUid() {
        return groupUid;
    }

    public void setGroupUid(long groupUid) {
        this.groupUid = groupUid;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
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

    public String getRuleDb() {
        return JsonUtil.toJson(this.rule);
    }

    public void setRule(HashMap<String, Integer> rule) {
        this.rule = rule;
    }

    public void setRuleDb(String rule) {
        this.rule = JsonUtil.fromJson(rule, new TypeReference<HashMap<String, Integer>>() {});
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getCurBureau() {
        return curBureau;
    }

    public void setCurBureau(int curBureau) {
        this.curBureau = curBureau;
    }

    public int getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(int ownerType) {
        this.ownerType = ownerType;
    }
}
