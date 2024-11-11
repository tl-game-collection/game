package com.xiuxiu.app.server.services.gateway.handler.room;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.message.Handler;

public class RoomDeskInfoHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        if (-1 == player.getRoomId()) {
            player.send(CommandId.CLI_NTF_ROOM_DESK_INFO_FAIL, ErrorCode.PLAYER_ROOM_NOT_IN);
            return null;
        }
        Room room = RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            player.send(CommandId.CLI_NTF_ROOM_DESK_INFO_FAIL, ErrorCode.PLAYER_ROOM_NOT_IN);
            return null;
        }
        room.syncDeskInfo(player);
        room.syncCurState(player);
        player.send(CommandId.CLI_NTF_ROOM_DESK_INFO_OK, null);
        return null;
    }
}
