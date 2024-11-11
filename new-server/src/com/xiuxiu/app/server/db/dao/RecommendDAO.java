package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.player.Recommend;

import java.util.List;

public interface RecommendDAO {
    boolean save(Recommend recommend);
    List<Recommend> load(long recommendPlayerUid, int begin, int page);
    List<Recommend> load(long recommendPlayerUid, long groupUid);
    List<Recommend> load(long groupUid);
    boolean changeState(long recommendPlayerUid, long groupUid, int oldState, int newState);
    List<Long> loadByReferrerUid(long rUid, int begin, int page);
}
