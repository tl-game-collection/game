package com.xiuxiu.app.server.score;

import com.xiuxiu.app.server.room.normal.IRoom;

/**
 * 战绩 接口
 * @author Administrator
 *
 */
public interface IRoomScore {
    
    void setUid(long uid);
    
    void setRoomUid(long roomUid);
    
    void setRoomId(int roomId);
    
    void setRoomType(int roomType);
    
    void setGameType(int gameType);
    
    void setGameSubType(int gameSubType);
    
    void setGroupUid(long groupUid);
    
    void setBeginTime(long now);
    
    void setEndTime(long endTime);
    
    void addRecord(ScoreInfo scoreInfo);
    
    void addScoreItemInfo(long playerId, int score, IRoom room);

}
