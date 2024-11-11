package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.statistics.TodayStatistics;

import java.util.List;

public interface ITodayStatisticsDAO extends IBaseDAO<TodayStatistics>{
    List<TodayStatistics> loadByFromUid(long fromUid, int statisticsType);
}
