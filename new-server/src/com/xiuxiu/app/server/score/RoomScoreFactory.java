package com.xiuxiu.app.server.score;

import com.xiuxiu.app.server.db.UIDManager;
import com.xiuxiu.app.server.db.UIDType;
import com.xiuxiu.app.server.room.ERoomType;
import com.xiuxiu.app.server.room.normal.IRoom;

public final class RoomScoreFactory {

    /**
     * 获取房间处理对象
     * 
     * @param room
     * @return
     */
    public static IRoomScore createRoomScore(IRoom room) {
        IRoomScore roomScore = null;
        if (ERoomType.NORMAL == room.getRoomType()) {
            roomScore = new RoomScore();
            roomScore.setUid(UIDManager.I.getAndInc(UIDType.SCORE_ROOM));
        } else if (ERoomType.BOX == room.getRoomType()) {
            roomScore = new BoxRoomScore();
            roomScore.setUid(UIDManager.I.getAndInc(UIDType.SCORE_BOX));
        }
        if (roomScore != null) {
            roomScore.setRoomUid(room.getRoomUid());
            roomScore.setRoomId(room.getRoomId());
            roomScore.setGameType(room.getGameType());
            roomScore.setGameSubType(room.getGameSubType());
            roomScore.setGroupUid(-1);
            roomScore.setBeginTime(System.currentTimeMillis());
        }
        return roomScore;
    }
}
