package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.rank.RankData;
import com.xiuxiu.app.server.statistics.TodayStatistics;

import java.util.List;

public interface IRankDataDAO extends IBaseDAO<RankData>{
    List<RankData> loadAll();
}
