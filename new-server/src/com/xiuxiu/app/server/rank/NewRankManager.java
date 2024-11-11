package com.xiuxiu.app.server.rank;

import com.xiuxiu.app.protocol.client.rank.PCLIRankNtfRankList;
import com.xiuxiu.app.server.BaseManager;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.core.utils.TimerHolder;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class NewRankManager extends BaseManager {
    private static class NewRankManagerHolder {
        private static NewRankManager instance = new NewRankManager();
    }
    public static NewRankManager I = NewRankManagerHolder.instance;

    /**
     * 所有排行榜数据
     * key ERankType value value-key rankData.getFromUid() value-value rankData
     */
    private HashMap<Integer,ConcurrentHashMap<Long,RankData>> allRankDataMap = new HashMap<>();

    private NewRankManager() {
    }

    public void init(){
        loadAllRankData();
        timerCheckAllRank();
    }

    private void loadAllRankData(){
        for (ERankType rankType : ERankType.values()){
            allRankDataMap.put(rankType.ordinal(),new ConcurrentHashMap<>());
        }

        List<RankData> rankDataList =  DBManager.I.getRankDataDao().loadAll();
        for (RankData rankData : rankDataList){
            addRankData(rankData);
        }
    }

    private ConcurrentHashMap<Long,RankData> getRankDataMapByRankType(int rankType){
        return  allRankDataMap.get(rankType);
    }

    private void addRankData(RankData rankData){
        ConcurrentHashMap<Long,RankData> dataMap = this.getRankDataMapByRankType(rankData.getRankType());
        dataMap.put(rankData.getFromUid(),rankData);
    }

    private void updateRank(int rankType,long fromUid,long playerUid,int value,long lastTime){
        ConcurrentHashMap<Long,RankData> dataMap = getRankDataMapByRankType(rankType);
        RankData rankData = dataMap.get(fromUid);
        if (rankData == null){
            rankData = RankData.create(fromUid,rankType);
            dataMap.putIfAbsent(fromUid,rankData);
            rankData = dataMap.get(fromUid);
        }
        rankData.updateRank(playerUid,value,lastTime);
    }

    private void checkAllRank(long nowTime){
        RankData rankData = null;
        for (ConcurrentHashMap<Long,RankData> dataMap : allRankDataMap.values()){
            Iterator<Map.Entry<Long, RankData>> it = dataMap.entrySet().iterator();
            while (it.hasNext()){
                rankData = it.next().getValue();
                if (null != rankData){
                    rankData.updateRankData(nowTime);
                }
            }
        }
    }

    private void timerCheckAllRank() {
        TimerHolder.getTimer().newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                long nowTime = System.currentTimeMillis();
                checkAllRank(nowTime);
                timerCheckAllRank();
            }
        }, 5, TimeUnit.MINUTES);
    }

    public void zero(long now){
        checkAllRank(now);
    }

    @Override
    public int save() {
        try {
            int cnt = 0;
            RankData rankData = null;
            for (ConcurrentHashMap<Long,RankData> dataMap : this.allRankDataMap.values()){
                Iterator<Map.Entry<Long, RankData>> it = dataMap.entrySet().iterator();
                while (it.hasNext()){
                    rankData = it.next().getValue();
                    if (null != rankData){
                        if (rankData.save()){
                            cnt++;
                        }
                    }
                }
            }
            return cnt;
        } catch(Exception e) {
            Logs.RANK.error(e);
        }
        return 0;
    }

    @Override
    public int shutdown() {
        return 0;
    }

    public void updateClubGameRank(ERankType rankType,long mainUid,long fromUid,long playerUid,int value,long lastTime){
        //delete someone in Config.NORANK
        if (Config.checkNoRankHas(playerUid)) {
            return;
        }
        this.updateRank(rankType.ordinal(),fromUid,playerUid,value,lastTime);
        if (mainUid > 0){
            if (ERankType.CLUB_GAME_NUM == rankType) {
                this.updateRank(ERankType.CLUB_MAIN_GAME_NUM.ordinal(), mainUid, playerUid, value, lastTime);
            }else if (ERankType.CLUB_GAME_WINNER == rankType){
                this.updateRank(ERankType.CLUB_MAIN_GAME_WINNER.ordinal(), mainUid, playerUid, value, lastTime);
            }else if (ERankType.CLUB_GAME_SCORE== rankType){
                this.updateRank(ERankType.CLUB_MAIN_GAME_SCORE.ordinal(),mainUid,playerUid,value,lastTime);
            }
        }
    }
    public PCLIRankNtfRankList getPCLIRankNtfRankList(long fromUid,ERankType rankType,int type,int page,long playerUid,String selfValue){
        PCLIRankNtfRankList rankList = new PCLIRankNtfRankList();
        rankList.page = page;
        rankList.hasNext = false;
        rankList.sValue = selfValue;

        ConcurrentHashMap<Long,RankData> dataMap = this.getRankDataMapByRankType(rankType.ordinal());
        if (null == dataMap){
            return rankList;
        }
        RankData rankData = dataMap.get(fromUid);
        if (null != rankData){
            rankData.fillRankList(rankList,type,playerUid);
        }
        return rankList;
    }
}
