package com.xiuxiu.app.server.db.dao;

import com.xiuxiu.app.server.score.BoxRoomScore;

import java.util.List;

public interface BoxRoomScoreDAO extends IBaseDAO<BoxRoomScore> {
    boolean create(BoxRoomScore boxRoomScore);
    List<BoxRoomScore> loadByGroupUid(long groupUid, long playerUid, long beginTime, int page, int pageSize);
    List<BoxRoomScore> loadByGroupUid(long groupUid, long playerUid, long beginTime, long endTime, int page, int pageSize);
    
    List<BoxRoomScore> loadByGroupUidAndRoomId(long groupUid, long playerUid, long beginTime, int page, int pageSize, int roomId);
    List<BoxRoomScore> loadByGroupUidAndRoomId(long groupUid, long playerUid, long beginTime, long endTime, int page, int pageSize, int roomId);
    
    
    List<BoxRoomScore> loadByRoomAndGameType(long groupUid, long beginTime, int page, int pageSize, int roomId, int gameType, int gameSubType, long playerUid);
    List<BoxRoomScore> loadByRoomAndGameType(long groupUid, long beginTime, long endTime, int page, int pageSize, int roomId, int gameType, int gameSubType, long playerUid);
    
    List<BoxRoomScore> loadByGameType(long groupUid, long beginTime, int page, int pageSize, int gameType, int gameSubType, long playerUid);
    List<BoxRoomScore> loadByGameType(long groupUid, long beginTime, long endTime, int page, int pageSize, int gameType, int gameSubType, long playerUid);
}
