 package com.xiuxiu.app.server.room.normal.poker.baccarat;

import com.xiuxiu.app.server.room.GameInfo;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.normal.RoomInfo;
import com.xiuxiu.app.server.room.normal.Hundred.HundredRoom;

/**
 * 百家乐房间实现
 */
@GameInfo(gameType = GameType.GAME_TYPE_HUNDRED_BACCARAT)
public class BaccaratRoom extends HundredRoom {

    public BaccaratRoom(RoomInfo info) {
        super(info);
    }


}
