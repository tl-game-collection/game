package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.club.ClubGoldRecord;

import java.util.List;

public interface IClubGoldRecordDAO extends IBaseDAO<ClubGoldRecord> {
    List<ClubGoldRecord> loadClubGoldRecordByClubUid(long clubUid, int action, int begin, int pageSize);
    List<ClubGoldRecord> loadClubGoldRecordByClubUidAndTime(long clubUid, int action, long time, int begin, int pageSize);

    /**
     * 加载上下分操作的记录
     * @param clubUid
     * @param playerUid
     * @param begin
     * @param pageSize
     * @return
     */
    List<ClubGoldRecord> loadSetGoldRecord(long clubUid, long playerUid, int begin, int pageSize,long minTime);
    //int loadClubGoldRecordCounInMoney(long leagueUid,int action);
}
