package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.score.BoxArenaScoreInfo;
import com.xiuxiu.app.server.score.BoxArenaScoreInfoPlayerId;

import java.util.List;

public interface IBoxArenaScoreInfoPlayerIdDao extends IBaseDAO<BoxArenaScoreInfoPlayerId>{
    List<BoxArenaScoreInfoPlayerId> loadAll(Long uid);
}
