package com.xiuxiu.app.server.db.dao;

import java.util.List;
import java.util.Map;

import com.xiuxiu.app.server.statistics.LogAccount;

public interface ILogAccountDAO extends IBaseDAO<LogAccount> {
    void createMultiple(List<LogAccount> logs);
    List<LogAccount> load(long targetUid, int action, long timeBegin, long timeEnd, long limitOffset, int limitCount);
    long count(long targetUid, int action, long timeBegin, long timeEnd);
    List<Long> loadTimeByAction(int action, long timeBegin, long timeEnd);
    LogAccount getLastRecordByAction(int action);
    List<Map<String, Object>> getDailyActive(long timeBegin, long timeEnd);
    int loadLoginByTimeAndTargetUids(long timeBegin, long timeEnd, List<Long> targetUidList);
    List<Long> loadYesterdayRemain(long timeBegin, long timeEnd);
    int loadRegisterNumByTime(long timeBegin, long timeEnd);
}
