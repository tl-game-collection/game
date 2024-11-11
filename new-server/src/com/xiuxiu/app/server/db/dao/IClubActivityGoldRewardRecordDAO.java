package com.xiuxiu.app.server.db.dao;

import java.util.List;

import com.xiuxiu.app.server.club.activity.gold.ClubActivityGoldRewardRecord;

public interface IClubActivityGoldRewardRecordDAO extends IBaseDAO<ClubActivityGoldRewardRecord> {

    int loadCountGold(long clubUid);

    List<ClubActivityGoldRewardRecord> loadByClubUid(long clubUid, int begin, int pageSize);

    List<ClubActivityGoldRewardRecord> loadByClubUidAndBoxUid(long clubUid, long boxUid, int begin, int pageSize);

    List<ClubActivityGoldRewardRecord> loadByClubUidAndBoxUidAndStartTimeAndEndTime(long clubUid, long boxUid, long startTime, long endTime, int begin, int pageSize);
}
