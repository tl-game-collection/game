package com.xiuxiu.app.server.forbid;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.forbid.PCLIForbidNtfInfo;
import com.xiuxiu.app.server.BaseManager;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;

/**
 * 防作弊管理器
 * 
 * @author Administrator
 *
 */
public class ForbidManager extends BaseManager {

    private static class GroupForbidManagerHolder {
        private static final ForbidManager INSTANCE = new ForbidManager();
    }

    public static ForbidManager I = GroupForbidManagerHolder.INSTANCE;

    /**
     * 防作弊缓存,格式：map<类型,map<群id,防作弊群组信息>>
     */
    private Map<EClubType, Map<Long, ForbidClubData>> forbidMap = new ConcurrentHashMap<>();

    /**
     * 加载并初始化防作弊信息
     */
    public void loadAll() {
        List<Forbid> forbidList = DBManager.I.getForbidDAO().loadAll();
        initForbids(forbidList);
    }

    /**
     * 初始化防作弊信息
     * 
     * @param forbidList
     */
    private void initForbids(List<Forbid> forbidList) {
        if (null == forbidList || forbidList.size() == 0) {
            return;
        }
        for (Forbid forbid : forbidList) {
            add(forbid);
        }
    }

    /**
     * 是否已经存在该组玩家的防作弊信息
     * 
     * @param type
     * @param clubId
     * @param playerIds
     * @return
     */
    public boolean isExist(EClubType type, long clubId, Long... playerIds) {
        if (forbidMap.containsKey(type)) {
            Map<Long, ForbidClubData> tempGroupMap = forbidMap.get(type);
            if (tempGroupMap.containsKey(clubId)) {
                return tempGroupMap.get(clubId).isExist(playerIds);
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 添加防作弊信息
     * 
     * @param
     */
    public void add(Forbid forbid) {
        EClubType type = EClubType.getType(forbid.getClubType());
        if (null == type) {
            return;
        }
        Map<Long, ForbidClubData> tempMap = null;
        if (forbidMap.containsKey(type)) {
            tempMap = forbidMap.get(type);
        } else {
            tempMap = new HashMap<Long, ForbidClubData>();
        }
        forbidMap.put(type, tempMap);
        ForbidClubData groupData = null;
        if (tempMap.containsKey(forbid.getClubUid())) {
            groupData = tempMap.get(forbid.getClubUid());
        } else {
            groupData = new ForbidClubData();
        }
        tempMap.put(forbid.getClubUid(), groupData);
        groupData.addForbid(forbid);
    }

    public ErrorCode isForbid(int type, long clubId, long playerId, Collection<Long> playerIds) {
        for (Long tempPlayerId : playerIds) {
            if (null == tempPlayerId) {
                continue;
            }
            if (playerId == tempPlayerId) {
                continue;
            }
            if (isForbid(type, clubId, playerId, tempPlayerId)) {
                Logs.ROOM.warn("%s %s 已经添加防作弊无法加入", String.valueOf(playerId), String.valueOf(tempPlayerId));
                return ErrorCode.FORBID_SAME_JOIN;
            }
        }
        return ErrorCode.OK;
    }

    /**
     * 判断玩家是否已经被添加到防作弊列表
     * 
     * @param type
     * @param clubId
     * @param playerUid1
     * @param playerUid2
     * @return
     */
    public boolean isForbid(int type, long clubId, long playerUid1, long playerUid2) {
        EClubType clubType = EClubType.getType(type);
        if (forbidMap.containsKey(clubType)) {
            Map<Long, ForbidClubData> tempGroupMap = forbidMap.get(clubType);
            IClub club=ClubManager.I.getClubByUid(clubId);
            if(club.checkIsMainClub()){
                boolean isTrue=false;
                List<Long> uidList=new ArrayList<>();
                club.fillDepthChildClubUidList(uidList);
                uidList.add(clubId);
                for (int i = 0; i < uidList.size(); i++) {
                    long uid = uidList.get(i);
                    IClub club1 = ClubManager.I.getClubByUid(uid);
                    if (tempGroupMap.containsKey(club1.getClubUid())) {
                        boolean flag=tempGroupMap.get(club1.getClubUid()).isForbid(playerUid1, playerUid2);
                        if(flag){
                            isTrue=true;
                            break;
                        }
                    }
                }
                return  isTrue;
            }else{
                if (tempGroupMap.containsKey(clubId)) {
                    return tempGroupMap.get(clubId).isForbid(playerUid1, playerUid2);
                }
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 删除防作弊信息
     * 
     * @param forbid
     */
    public void remove(Forbid forbid) {
        EClubType type = EClubType.getType(forbid.getClubType());
        if (forbidMap.containsKey(type)) {
            Map<Long, ForbidClubData> tempGroupMap = forbidMap.get(type);
            if (tempGroupMap.containsKey(forbid.getClubUid())) {
                tempGroupMap.get(forbid.getClubUid()).removeForbid(forbid);
            }
        }
    }

    /**
     * 根据群或联盟获取防作弊数据
     */
    public Map<Long, Forbid> getForbidsByTypeAndUid(EClubType type, long fromUid){
        Map<Long, ForbidClubData> tempGroupMap = forbidMap.get(type);
        if (null != tempGroupMap) {
            ForbidClubData forbidGroupData = tempGroupMap.get(fromUid);
            if (null != forbidGroupData){
                return forbidGroupData.getIdsMap();
            }
        }
        return null;
    }

    public void removeForbidByMerge(long clubUid,EClubType type){
        Map<Long, ForbidClubData> tempGroupMap = forbidMap.get(type);
        if (null != tempGroupMap) {
            ForbidClubData forbidGroupData = tempGroupMap.get(clubUid);
            if (null != forbidGroupData){
                Map<Long, Forbid> map=forbidGroupData.getIdsMap();
                for (Iterator<Map.Entry<Long, Forbid>> it = map.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<Long, Forbid> tempEntry = it.next();
                   Forbid forbid = tempEntry.getValue();
                   if(forbid.isFlag()){
                       forbidGroupData.removeForbid(forbid);
                       DBManager.I.save(() -> {
                           DBManager.I.getForbidDAO().delByUid(forbid.getUid());
                       });
                   }
                }
            }
        }

    }
    public void removeForbidByPlayerLeave(long playerUid,long clubUid,EClubType type){
        Map<Long, ForbidClubData> tempGroupMap = forbidMap.get(type);
        if (null != tempGroupMap) {
            ForbidClubData forbidGroupData = tempGroupMap.get(clubUid);
            if (null != forbidGroupData){
                Map<Long, Forbid> map=forbidGroupData.getIdsMap();
                for (Iterator<Map.Entry<Long, Forbid>> it = map.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<Long, Forbid> tempEntry = it.next();
                    Forbid forbid = tempEntry.getValue();
                    Long[] tempIds = forbid.getPlayerUidList();
                    for (long tempPlayerUid : tempIds){
                        if(playerUid==tempPlayerUid){
                            forbidGroupData.removeForbid(forbid);
                            DBManager.I.getForbidDAO().delByUid(forbid.getUid());
                        }
                    }
                }
            }
        }
    }
    @Override
    public int save() {
        return 0;
    }

    @Override
    public int shutdown() {
        return 0;
    }

}
