package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.score.BoxArenaScoreInfo;

import java.util.List;

public interface IBoxArenaScoreInfoDao extends IBaseDAO<BoxArenaScoreInfo>{
    List<BoxArenaScoreInfo> loadAll(List<Long> uidList);
    List<BoxArenaScoreInfo> loadAllByBoxUid(Long playerUid,long boxUid,int page, int pageSize) ;
    /**
     * 获取牛牛战绩列表
     * @param playerUid
     * @param page
     * @param pageSize
     * @return
     */
    List<BoxArenaScoreInfo> loadAllByPlayerUid(Long playerUid,int page, int pageSize) ;
}