package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.score.RoomScore;

import java.util.List;

public interface RoomScoreDAO extends IBaseDAO<RoomScore> {
    boolean create(RoomScore roomScore);
    List<RoomScore> loadByPlayerUid(long playerUid, long beginTime, int page, int pageSize,int gameType,int gameSubType);
    List<RoomScore> loadByPlayerUid(long playerUid, long beginTime, long endTime, int page, int pageSize,int gameType,int gameSubType);
    List<RoomScore> loadByGroupUidWithPlayerUid(long groupUid, long playerUid, long beginTime, int page, int pageSize,int gameType,int gameSubType);
    List<RoomScore> loadByGroupUidWithPlayerUid(long groupUid, long playerUid, long beginTime, long endTime, int page, int pageSize,int gameType,int gameSubType);
}
