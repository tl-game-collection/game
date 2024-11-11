package com.xiuxiu.app.server.db.dao;

import java.util.List;

import com.xiuxiu.app.server.room.normal.Hundred.HundredRebRecordInfo;

public interface IHundredRebRecordDAO extends IBaseDAO<HundredRebRecordInfo> {
    List<HundredRebRecordInfo> loadByPlayerUid(long roomId, long playerUid, int begin, int size);
    boolean saveAll(List<HundredRebRecordInfo> list);

    List<HundredRebRecordInfo> loadByClubUid(long clubUid,long startTime,long endTime);
}
