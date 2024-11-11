package com.xiuxiu.app.server.rank;

import com.alibaba.fastjson.TypeReference;
import com.xiuxiu.app.protocol.client.rank.PCLIRankNtfRankList;
import com.xiuxiu.app.server.db.BaseTable;
import com.xiuxiu.app.server.db.ETableType;
import com.xiuxiu.app.server.db.UIDManager;
import com.xiuxiu.app.server.db.UIDType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.StringUtil;
import com.xiuxiu.core.utils.TimeUtil;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RankData extends BaseTable {
    private long fromUid;    //排行榜来源 例如亲友圈uid
    private int rankType;    //排行榜类型
    private long updateTime; //更新时间
    private List<NewRankItem> todayRanks = new ArrayList<>(); //今天的排行榜
    private List<NewRankItem> yesterdayRanks = new ArrayList<>(); //昨天的排行榜
    private List<NewRankItem> anteayerRanks = new ArrayList<>(); //前天的排行榜

    private transient volatile int todayRankMinValue = 0;   //今日排行榜最小数值
    private transient volatile boolean needUpdateRank = true; //是否需要重新排序
    private transient Map<Long,NewRankItem> waitRankMaps = new HashMap<>(); //等待重新排序的数据
    private transient ReadWriteLock rwLock = new ReentrantReadWriteLock();

    private static final int RANK_MAX_NUM = 100; //排多少名
    public static final int RANK_TODAY = 0;                // 今日
    public static final int RANK_YESTERDAY = 1;            // 昨日
    public static final int RANK_ANTEAYER = 2;             // 前天

    public RankData() {
        this.tableType = ETableType.TB_RANK_DATA;
    }

    public long getFromUid() {
        return fromUid;
    }

    public void setFromUid(long fromUid) {
        this.fromUid = fromUid;
    }

    public int getRankType() {
        return rankType;
    }

    public void setRankType(int rankType) {
        this.rankType = rankType;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getTodayRanks() {
        return JsonUtil.toJson(this.todayRanks);
    }
    public void setTodayRanks(String todayRanksStr) {
        if (StringUtil.isEmptyOrNull(todayRanksStr)) {
            return;
        }
        ArrayList<NewRankItem> temp = JsonUtil.fromJson(todayRanksStr, new TypeReference<ArrayList<NewRankItem>>() {});
        if (null != temp) {
            this.todayRanks = temp;
        }
    }

    public String getYesterdayRanks() {
        return JsonUtil.toJson(this.yesterdayRanks);
    }
    public void setYesterdayRanks(String yesterdayRanksStr) {
        if (StringUtil.isEmptyOrNull(yesterdayRanksStr)) {
            return;
        }
        ArrayList<NewRankItem> temp = JsonUtil.fromJson(yesterdayRanksStr, new TypeReference<ArrayList<NewRankItem>>() {});
        if (null != temp) {
            this.yesterdayRanks = temp;
        }
    }

    public String getAnteayerRanks() {
        return JsonUtil.toJson(this.anteayerRanks);
    }
    public void setAnteayerRanks(String anteayerRanksStr) {
        if (StringUtil.isEmptyOrNull(anteayerRanksStr)) {
            return;
        }
        ArrayList<NewRankItem> temp = JsonUtil.fromJson(anteayerRanksStr, new TypeReference<ArrayList<NewRankItem>>() {});
        if (null != temp) {
            this.anteayerRanks = temp;
        }
    }

    public void updateRank(long playerUid,int value,long lastTime){
        try {
            this.rwLock.readLock().lock();
            if (ERankType.CLUB_GAME_SCORE.ordinal() != this.rankType
                && this.todayRanks.size() >= RANK_MAX_NUM
                    && this.todayRankMinValue >= value){
                return;
            }

            NewRankItem item = this.waitRankMaps.get(playerUid);
            if (null == item) {
                this.waitRankMaps.put(playerUid, NewRankItem.create(playerUid, value, lastTime));
            } else {
                item.setVe(value);
                item.setlT(lastTime);
            }
            needUpdateRank = true;
            this.setDirty(true);
        }finally {
            this.rwLock.readLock().unlock();
        }
    }

    public void updateRankData(long nowTime){
        try {
            this.rwLock.writeLock().lock();

            //跨天更新
            if (!TimeUtil.isSameDay(this.getUpdateTime(), nowTime)) {
                this.setUpdateTime(nowTime);
                this.anteayerRanks.clear();
                List<NewRankItem> temp = this.anteayerRanks;
                this.anteayerRanks = this.yesterdayRanks;
                this.yesterdayRanks = this.todayRanks;
                this.todayRanks = temp;

                //TODO 先简单粗暴的把等待排序的玩家清空
                this.waitRankMaps.clear();
                this.needUpdateRank = false;
                this.setDirty(true);
                return;
            }

            if (!needUpdateRank){
                return;
            }

            for (NewRankItem item : this.todayRanks) {
                if (!waitRankMaps.containsKey(item.getpUid())){
                    waitRankMaps.put(item.getpUid(), NewRankItem.create(item.getpUid(), item.getVe(), item.getlT()));
                }
            }

            ArrayList<Map.Entry<Long, NewRankItem>> list = new ArrayList<>(waitRankMaps.entrySet());
            sortByValue(list);

            int rankIndex = 0;
            NewRankItem oldRankItem = null;
            NewRankItem newRankItem = null;
            for (int i = 0; i < (RANK_MAX_NUM > list.size() ? list.size() : RANK_MAX_NUM); i++) {
                newRankItem = list.get(i).getValue();
                if (newRankItem.getVe() > 0){
                    if (this.todayRanks.size() > rankIndex){
                        oldRankItem = this.todayRanks.get(rankIndex);
                        oldRankItem.setpUid(newRankItem.getpUid());
                        oldRankItem.setVe(newRankItem.getVe());
                        oldRankItem.setlT(newRankItem.getlT());
                    }else{
                        this.todayRanks.add(NewRankItem.create(newRankItem.getpUid(),newRankItem.getVe(),newRankItem.getlT()));
                    }
                    rankIndex++;
                }
            }

            //分数排行榜因为分数可以减少所以不能清空waitRankMaps,当榜上玩家分数减少需要waitRankMaps的成员替补上
            if (this.rankType != ERankType.CLUB_GAME_SCORE.ordinal() && this.waitRankMaps.size() > RANK_MAX_NUM*2){
                this.waitRankMaps.clear();
            }

            this.needUpdateRank = false;
            this.setDirty(true);

        } finally {
            this.rwLock.writeLock().unlock();
        }
    }

    private void sortByValue(ArrayList<Map.Entry<Long, NewRankItem>> list) {
        Collections.sort(list, new Comparator<Map.Entry<Long, NewRankItem>>() {
            @Override
            public int compare(Map.Entry<Long, NewRankItem> o1, Map.Entry<Long, NewRankItem> o2) {
                // 降序
                if ( o2.getValue().getVe() < o1.getValue().getVe()){
                    return -1;
                }
                if (o2.getValue().getVe() == o1.getValue().getVe()){
                    return o2.getValue().getlT() < o1.getValue().getlT() ? -1 : o2.getValue().getlT() == o1.getValue().getlT() ? 0 : 1;
                }
                return 1;
            }
        });
    }

    public void fillRankList(PCLIRankNtfRankList rankList,int type,long playerUid){
        try {
            this.rwLock.readLock().lock();
            List<NewRankItem> list = null;
            if (type == RANK_TODAY){
                list = this.todayRanks;
            }else if (type == RANK_YESTERDAY){
                list = this.yesterdayRanks;
            }else {
                list = this.anteayerRanks;
            }

            for (int i = 0; i < list.size(); i++) {
                NewRankItem rank = list.get(i);
                Player rankPlayer = PlayerManager.I.getPlayer(rank.getpUid());
                if (null == rankPlayer) {
                    continue;
                }
                PCLIRankNtfRankList.RankInfo rankInfo = new PCLIRankNtfRankList.RankInfo();
                rankInfo.name = rankPlayer.getName();
                rankInfo.playerUid = rank.getpUid();
                rankInfo.value = rank.getVe();
                rankInfo.lastTime = rank.getlT();
                if (rankInfo.playerUid == playerUid){
                    rankList.sValue = String.valueOf(rankInfo.value);
                }
                rankList.list.add(rankInfo);
            }
        }finally {
            this.rwLock.readLock().unlock();
        }
    }


    public static RankData create(long fromUid,int rankType){
        RankData data = new RankData();
        data.setUid(UIDManager.I.getAndInc(UIDType.RANK_DATA));
        data.setFromUid(fromUid);
        data.setRankType(rankType);
        data.setUpdateTime(System.currentTimeMillis());
        return data;
    }
}
