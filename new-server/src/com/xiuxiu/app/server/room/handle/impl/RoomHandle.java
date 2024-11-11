package com.xiuxiu.app.server.room.handle.impl;

import com.xiuxiu.app.server.room.handle.AbstractRoomHandle;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.score.IRoomScore;
import com.xiuxiu.app.server.score.ScoreInfo;
import com.xiuxiu.app.server.score.ScoreItemInfo;

/**
 * 大厅房间默认处理器
 * 
 * @author Administrator
 *
 */
public class RoomHandle extends AbstractRoomHandle {

    public RoomHandle(IRoom room) {
        super(room);
    }

}
