package com.xiuxiu.app.server.score;

import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.StringUtil;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @auther: yuyunfei
 * @date: 2020/1/7 17:01
 * @comment:
 */
public class BoxArenaScore extends BaseTable {
    private long boxUid;                                                            // 包厢UID
    private int gameType;                                                           // 游戏类型
    private int gameSubType;                                                        // 游戏子类型
    private long clubUid;                                                           // 俱乐部UID(参与游戏的俱乐部UID)
    private long playerUid;                                                         // 玩家UID
    private long beginTime;                                                         // 开始时间
    private long endTime;                                                           // 结束时间
    private AtomicInteger bureau = new AtomicInteger(0);                  // 局数
    private AtomicInteger score = new AtomicInteger(0);                   // 分数
    private ArrayList<Long> recordUid = new ArrayList<>();                          // 战绩列表
    private ConcurrentHashMap<String, Integer> allCnt = new ConcurrentHashMap<>();  // 大结算记录

    public BoxArenaScore() {
        this.tableType = ETableType.TB_BOX_ARENA_SCORE;
    }

    public void bureauInc() {
        this.bureau.incrementAndGet();
    }

    public void addScore(int value) {
        this.score.addAndGet(value);
    }

    public void addRecord(long recordUid) {
        this.recordUid.add(recordUid);
    }

    public long getBoxUid() {
        return boxUid;
    }

    public void setBoxUid(long boxUid) {
        this.boxUid = boxUid;
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

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getBureau() {
        return bureau.get();
    }

    public void setBureau(int bureau) {
        this.bureau.set(bureau);
    }

    public int getScore() {
        return score.get();
    }

    public void setScore(int score) {
        this.score.set(score);
    }

    public ArrayList<Long> getRecordUid() {
        return recordUid;
    }

    public void setRecordUid(ArrayList<Long> recordUid) {
        this.recordUid = recordUid;
    }

    @JSONField(serialize = false)
    public String getRecordUidDb() {
        return JsonUtil.toJson(this.recordUid);
    }

    @JSONField(serialize = false)
    public void setRecordUidDb(String recordUid) {
        if (StringUtil.isEmptyOrNull(recordUid)) {
            return;
        }
        ArrayList<Long> temp = JsonUtil.fromJson(recordUid, new TypeReference<ArrayList<Long>>() {
        });
        if (null != temp) {
            this.recordUid = temp;
        }
    }

    public ConcurrentHashMap<String, Integer> getAllCnt() {
        return allCnt;
    }

    public void setAllCnt(ConcurrentHashMap<String, Integer> allCnt) {
        this.allCnt = allCnt;
    }

    @JSONField(serialize = false)
    public String getAllCntDb() {
        return JsonUtil.toJson(this.allCnt);
    }

    @JSONField(serialize = false)
    public void setAllCntDb(String value) {
        if (StringUtil.isEmptyOrNull(value)) {
            return;
        }
        ConcurrentHashMap<String, Integer> temp = JsonUtil.fromJson(value, new TypeReference<ConcurrentHashMap<String, Integer>>() {
        });
        if (null != temp) {
            this.allCnt = temp;
        }
    }

    @Override
    public String toString() {
        return "BoxArenaScore{" +
                "boxUid=" + boxUid +
                ", gameType=" + gameType +
                ", gameSubType=" + gameSubType +
                ", clubUid=" + clubUid +
                ", playerUid=" + playerUid +
                ", beginTime=" + beginTime +
                ", endTime=" + endTime +
                ", bureau=" + bureau +
                ", score=" + score +
                ", recordUid=" + recordUid +
                ", allCnt=" + allCnt +
                ", isNew=" + isNew +
                ", tableType=" + tableType +
                ", uid=" + uid +
                ", dirty=" + dirty +
                '}';
    }
}
