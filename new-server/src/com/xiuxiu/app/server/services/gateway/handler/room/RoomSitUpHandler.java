package com.xiuxiu.app.server.services.gateway.handler.room;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.core.net.message.Handler;

public class RoomSitUpHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        IRoom room = RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_ROOM_SITUP_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }
        ErrorCode errorCode = room.sitUp(player);
        if (ErrorCode.OK != errorCode) {
            player.send(CommandId.CLI_NTF_ROOM_SITUP_FAIL, errorCode);
        } else {
            player.send(CommandId.CLI_NTF_ROOM_SITUP_OK, null);
        }
        return null;
    }
}
