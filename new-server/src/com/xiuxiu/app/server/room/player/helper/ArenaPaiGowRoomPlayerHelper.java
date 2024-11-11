package com.xiuxiu.app.server.room.player.helper;

import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.Room;

public class ArenaPaiGowRoomPlayerHelper extends ArenaRoomPlayerHelper {

    public ArenaPaiGowRoomPlayerHelper(IRoomPlayer roomPlayer) {
        super(roomPlayer);
    }

    @Override
    public int getCurBureau() {
        Room room = RoomManager.I.getRoom(roomPlayer.getRoomId());
        return null == room ? -1 : room.getCurBureau();
    }
}
