package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.club.ClubUid;
import com.xiuxiu.app.server.statistics.DownLineGameRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IDownLineGameRecordDAO extends IBaseDAO<DownLineGameRecord> {
    List<DownLineGameRecord> loadOneDayRecord(long zeroTime);
}
