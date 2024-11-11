package com.xiuxiu.app.server.services.gateway.handler.room;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.room.PCLIRoomReqDissolveInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.message.Handler;

public class RoomDissolveHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIRoomReqDissolveInfo info = (PCLIRoomReqDissolveInfo) request;
        Room room = RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_ROOM_DISSOLVE_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }
        
        ErrorCode errorCode = room.dissolve(player, 1 == info.force ? true : false);
        if (ErrorCode.OK != errorCode) {
            player.send(CommandId.CLI_NTF_ROOM_DISSOLVE_FAIL, errorCode);
        }
        return null;
    }
}
