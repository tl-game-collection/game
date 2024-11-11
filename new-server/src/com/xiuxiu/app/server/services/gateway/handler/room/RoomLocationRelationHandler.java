package com.xiuxiu.app.server.services.gateway.handler.room;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfLocationRelationInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.message.Handler;

public class RoomLocationRelationHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        Room room = RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 没有在房间内", player);
            return null;
        }
        PCLIRoomNtfLocationRelationInfo info = room.getLocationRelationInfo();
        player.send(CommandId.CLI_NTF_ROOM_LOCATION_RELATION_OK, info);
        return null;
    }
}
