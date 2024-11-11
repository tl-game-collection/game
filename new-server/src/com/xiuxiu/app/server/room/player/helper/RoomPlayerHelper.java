package com.xiuxiu.app.server.room.player.helper;

import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.Room;

public class RoomPlayerHelper extends AbstractRoomPlayerHelper {

    public RoomPlayerHelper(IRoomPlayer roomPlayer) {
        super(roomPlayer);
    }

    @Override
    public void init() {
        
    }
    
    @Override
    public int getCurBureau() {
        Room room = RoomManager.I.getRoom(roomPlayer.getRoomId());
        return null == room ? -1 : room.getCurBureau();
    }
}
