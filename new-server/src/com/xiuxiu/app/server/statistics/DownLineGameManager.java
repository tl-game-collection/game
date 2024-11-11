package com.xiuxiu.app.server.statistics;

import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.core.utils.TimeUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DownLineGameManager {
    private static class DownLineGameManagerHolder {
        private static DownLineGameManager instance = new DownLineGameManager();
    }

    public static DownLineGameManager I = DownLineGameManagerHolder.instance;

    /**
     * 缓存某个玩家的某个下级在某个群玩游戏的次数
     * key:(upLineUid上级玩家uid + ":" + 下级玩家uid + ":" + 群uid) value:keyValue(key:更新时间，value:每天游戏次数)
     */
    private ConcurrentHashMap<String, DownLineGameRecord> todayCache = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, DownLineGameRecord> yesterdayCache = new ConcurrentHashMap<>();

    private DownLineGameManager() {
    }

    public void updateDownLineGameInfo( long playerUid, long upLinePlayerUid, long clubUid,int gameCnt,int score,long nowTime){
        String key = this.getCacheKey(playerUid,upLinePlayerUid,clubUid);

        DownLineGameRecord data = todayCache.get(key);
        if (null == data){
            todayCache.putIfAbsent(key, DownLineGameRecord.create(upLinePlayerUid,playerUid,clubUid));
            data = todayCache.get(key);
        }

        synchronized (data){
            if (isSameDay(data.getZeroTime(),nowTime)){
                data.setCount(data.getCount() + gameCnt);
                data.setScore(data.getScore() + score);
            }else{
                if (data.isDirty()){
                    this.addToYesterdayCache(key,playerUid,upLinePlayerUid,clubUid,data.getCount(),data.getScore(),data.getZeroTime());
                }
                data.setCount(gameCnt);
                data.setScore(score);
                data.setZeroTime(TimeUtil.getZeroTimestamp(nowTime));
            }
            data.setDirty(true);
        }
    }

    private String getCacheKey(long playerUid, long upLinePlayerUid, long clubUid){
        return playerUid + ":" + upLinePlayerUid + ":" + clubUid;
    }

    private boolean isSameDay(long oneDayZeroTime,long checkTime){
        return (checkTime >= oneDayZeroTime && checkTime < (oneDayZeroTime + TimeUtil.ONE_DAY_MS));
    }

    private void addToYesterdayCache(String key, long playerUid, long upLinePlayerUid, long clubUid, int gameCnt,int score,long zeroTime){
        DownLineGameRecord record = yesterdayCache.get(key);
        if (null == record){
            record = DownLineGameRecord.create(upLinePlayerUid,playerUid,clubUid);
        }
        record.setCount(gameCnt);
        record.setScore(score);
        record.setZeroTime(zeroTime);
        record.setDirty(true);
        yesterdayCache.put(key,record);
    }

    private void saveYesterdayCache(long yesterdayZeroTime){
        DownLineGameRecord item = null;
        Map.Entry<String, DownLineGameRecord> data = null;
        Iterator<Map.Entry<String, DownLineGameRecord>> it = todayCache.entrySet().iterator();
        while (it.hasNext()){
            data = it.next();
            item = data.getValue();
            synchronized (item) {
                if (this.isSameDay(yesterdayZeroTime,item.getZeroTime()) && item.isDirty()) {
                    this.addToYesterdayCache(data.getKey(),item.getDownLineUid(),item.getPlayerUid(),item.getFromUid(),item.getCount(),item.getScore(),item.getZeroTime());
                    item.setDirty(false);
                }
            }
        }

        Iterator<Map.Entry<String, DownLineGameRecord>> iterator = yesterdayCache.entrySet().iterator();
        while (iterator.hasNext()){
            iterator.next().getValue().save();
        }
    }

    private void saveTodayCache(){
        Iterator<Map.Entry<String, DownLineGameRecord>> iterator = todayCache.entrySet().iterator();
        while (iterator.hasNext()){
            iterator.next().getValue().save();
        }
    }

    private void loadTodayCache(){
        List<DownLineGameRecord> records = DBManager.I.getDownLineGameRecordDAO().loadOneDayRecord(TimeUtil.getZeroTimestampWithToday());
        for (DownLineGameRecord record : records){
            String key = this.getCacheKey(record.getDownLineUid(),record.getPlayerUid(),record.getFromUid());
            todayCache.putIfAbsent(key,record);
        }
    }

    public void init(){
        this.loadTodayCache();
    }

    public void zero(long nowTime){
        this.saveYesterdayCache(TimeUtil.getZeroTimestamp( nowTime - TimeUtil.HALF_HOUR_MS));
    }

    public void save(){
        this.saveTodayCache();
    }
}
