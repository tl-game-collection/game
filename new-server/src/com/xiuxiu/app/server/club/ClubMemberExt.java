package com.xiuxiu.app.server.club;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.StringUtil;

public class ClubMemberExt extends BaseTable {
    private long clubUid;
    private long playerUid;
    private long gold = 0;         //金币
    private long rewardValue = 0; //rewardValue奖励值
    private long upTotalScore = 0;                //总上分（如果是群主就记录群主公共钱包的记录）
    private long downTotalScore = 0;              //总下分（如果是群主就记录群主公共钱包的记录）
    private int convert;                         // 兑换竞技分 0 没有兑换过 1 兑换过
    /**
     * 群推荐码
     */
    private long code = 0;
    /**
     * 玩家任务领奖集合<boxid,奖励状态>
     */
    private Map<Long, Integer> goldActivityStatus = new ConcurrentHashMap<>();

    /**
     * 财务下分订单最后一笔创建时间
     */
    transient private long orderCreateTime;

    /**
     * 上分财务最近被分配上分任务的时间
     */
    transient private long upGoldOrderLastTime;

    /**
     * 下分财务最近被分配下分任务的时间
     */
    transient private long downGoldOrderLastTime;

    public ClubMemberExt(){
        this.tableType = ETableType.TB_CLUB_MEMBER_EXT;
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

    public long getGold() {
        return gold;
    }

    public void setGold(long gold) {
        this.gold = gold;
    }

    public long getRewardValue() {
        return rewardValue;
    }

    public void setRewardValue(long rewardValue) {
        this.rewardValue = rewardValue;
    }

    public long getUpTotalScore() {
        return upTotalScore;
    }

    public void setUpTotalScore(long upTotalScore) {
        this.upTotalScore = upTotalScore;
    }

    public long getDownTotalScore() {
        return downTotalScore;
    }

    public void setDownTotalScore(long downTotalScore) {
        this.downTotalScore = downTotalScore;
    }
    
    public String getGoldActivityStatusDb() {
        return JSON.toJSONString(goldActivityStatus);
    }

    public void setGoldActivityStatusDb(String goldActivityStatus) {
        if (StringUtil.isEmptyOrNull(goldActivityStatus)) {
            return;
        }
        Map<Long, Integer> temp = JsonUtil.fromJson(goldActivityStatus, new TypeReference<Map<Long, Integer>>() {
        });
        if (null != temp) {
            this.goldActivityStatus = temp;
        }
    }

    public long getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(long orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    public long getUpGoldOrderLastTime() {
        return upGoldOrderLastTime;
    }

    public void setUpGoldOrderLastTime(long upGoldOrderLastTime) {
        this.upGoldOrderLastTime = upGoldOrderLastTime;
    }

    public long getDownGoldOrderLastTime() {
        return downGoldOrderLastTime;
    }

    public void setDownGoldOrderLastTime(long downGoldOrderLastTime) {
        this.downGoldOrderLastTime = downGoldOrderLastTime;
    }

    @JSONField(serialize = false)
    public Map<Long, Integer> getGoldActivityStatus() {
        return goldActivityStatus;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public int getConvert() {
        return convert;
    }

    public void setConvert(int convert) {
        this.convert = convert;
    }
}
