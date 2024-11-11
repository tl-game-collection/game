package com.xiuxiu.app.server.room.normal.Hundred;

import com.xiuxiu.app.server.room.GameInfo;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.normal.RoomInfo;

@GameInfo(gameType = GameType.GAME_TYPE_HUNDRED_LHD)
public class LhdHundredRoom extends HundredRoom {

    public LhdHundredRoom(RoomInfo info) {
        super(info);
    }

}
