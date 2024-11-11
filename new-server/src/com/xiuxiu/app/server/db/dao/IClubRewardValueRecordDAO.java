package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.club.ClubRewardValueRecord;

import java.util.List;

public interface IClubRewardValueRecordDAO extends IBaseDAO<ClubRewardValueRecord> {
    /**
     * 根据俱乐部UID、玩家UID 分页查询
     *
     * @param clubUid   俱乐部UID
     * @param playerUid 玩家UID
     * @param begin     开始页数
     * @param pageSize  每页几条
     * @return List<ClubRewardValueRecord>
     */
    List<ClubRewardValueRecord> loadByClubUid(long clubUid, long playerUid, int begin, int pageSize);

    /**
     * 根据俱乐部UID、玩家UID、时间 分页查询
     *
     * @param clubUid   俱乐部UID
     * @param playerUid 玩家UID
     * @param time      时间
     * @param begin     开始页数
     * @param pageSize  每页几条
     * @return List<ClubRewardValueRecord>
     */
    List<ClubRewardValueRecord> loadByPage(long clubUid, long playerUid, long time, int begin, int pageSize);

    List<ClubRewardValueRecord> loadDayDetails(long optTime);
}
