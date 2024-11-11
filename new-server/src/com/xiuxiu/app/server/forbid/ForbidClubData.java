package com.xiuxiu.app.server.forbid;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ForbidClubData {

    /**
     * 玩家关联的防作弊信息，格式：map<防作弊id,long[玩家id集合]>
     */
    private Map<Long,Forbid> idsMap = new ConcurrentHashMap<>();
    /**
     * 格式:map<sum(两个玩家玩家id和),List<防作弊id>>
     */
    private Map<Long, Set<Long>> sumIdsMap = new ConcurrentHashMap<>();

    /**
     * 是否已经存在防作弊信息
     * @param playerIds
     * @return
     */
    public boolean isExist(Long... playerIds) {
        for (Iterator<Map.Entry<Long,Forbid>> it = idsMap.entrySet().iterator(); it.hasNext();) {
            Long[] tempIds = it.next().getValue().getPlayerUidList();
            if (isExist(tempIds, playerIds)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 是否已经存在防作弊信息
     * @param tempIds
     * @param playerIds
     * @return
     */
    private boolean isExist(Long[] tempIds, Long... playerIds) {
        int count = 0;
        for (Long tempId : tempIds) {
            for (Long id : playerIds) {
                if (tempId.longValue() == id.longValue()) {
                    ++count;
                }
            }
        }
        return count >= playerIds.length;
    }

    /**
     * 添加防作弊信息
     * 
     * @param forbid
     */
    public void addForbid(Forbid forbid) {
        idsMap.put(forbid.getUid(), forbid);
        Long[] playerUids = forbid.getPlayerUidList();
        int size = playerUids.length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (playerUids[i].longValue() == playerUids[j].longValue()) {
                    continue;
                }
                long sum = playerUids[i] + playerUids[j];
                Set<Long> tempSumIdList = null;
                if (sumIdsMap.containsKey(sum)) {
                    tempSumIdList = sumIdsMap.get(sum);
                } else {
                    tempSumIdList = new HashSet<Long>();
                }
                sumIdsMap.put(sum, tempSumIdList);
                tempSumIdList.add(forbid.getUid());
            }
        }
    }

    /**
     * 删除防作弊信息
     * 
     * @param forbid
     */
    public void removeForbid(Forbid forbid) {
        idsMap.remove(forbid.getUid());
        for (Iterator<Map.Entry<Long, Set<Long>>> it = sumIdsMap.entrySet().iterator(); it.hasNext();) {
            for (Iterator<Long> iterator = it.next().getValue().iterator(); iterator.hasNext();) {
                if (iterator.next() == forbid.getUid()) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * 是否匹配防作弊信息
     * 
     * @param playerUid1
     * @param playerUid2
     * @return
     */
    public boolean isForbid(long playerUid1, long playerUid2) {
        if (sumIdsMap.isEmpty()) {
            return Boolean.FALSE;
        }
        long sumUid = playerUid1 + playerUid2;
        if (sumIdsMap.containsKey(sumUid)) {
            Set<Long> tempList = sumIdsMap.get(sumUid);
            if (null == tempList || tempList.isEmpty()) {
                return Boolean.FALSE;
            }
            for (Iterator<Long> iter = tempList.iterator(); iter.hasNext();) {
                Long tempUid = iter.next();
                if (idsMap.containsKey(tempUid)) {
                    Long[] tempPlayerUids = idsMap.get(tempUid).getPlayerUidList();
                    if (isForbid(tempPlayerUids, playerUid1, playerUid2)) {
                        return Boolean.TRUE;
                    }
                }
            }
        }
        return Boolean.FALSE;
    }

    private boolean isForbid(Long[] playerUids, long playerUid1, long playerUid2) {
        int count = 0;
        for (Long id : playerUids) {
            if (playerUid1 == id || playerUid2 == id) {
                ++count;
            }
        }
        return count == 2;
    }

    public Map<Long, Forbid> getIdsMap(){
        return this.idsMap;
    }
}
