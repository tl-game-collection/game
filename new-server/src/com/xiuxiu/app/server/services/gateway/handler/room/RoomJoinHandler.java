package com.xiuxiu.app.server.services.gateway.handler.room;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.room.PCLIRoomReqJoinInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.BoxManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.ERoomType;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.message.Handler;

public class RoomJoinHandler implements Handler {

    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIRoomReqJoinInfo info = (PCLIRoomReqJoinInfo) request;
        Room room = RoomManager.I.getRoom(info.roomId);
        if (room != null) {
            if (room.getRoomType() == ERoomType.ARENA) {
                player.send(CommandId.CLI_NTF_ROOM_JOIN_FAIL, ErrorCode.ROOM_NOT_EXISTS);
                return null;
            }
            if (room.getRoomType() == ERoomType.BOX && player.getRoomId() == -1) {
                player.send(CommandId.CLI_NTF_ROOM_JOIN_FAIL, ErrorCode.ROOM_NOT_EXISTS);
                return null;
            }
        }
        if (!RoomManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s roomId:%d 正在操作", player, info.roomId);
            player.send(CommandId.CLI_NTF_ROOM_JOIN_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            ErrorCode err = RoomManager.I.join(player, info.roomId);
            if (ErrorCode.OK != err) {
                player.send(CommandId.CLI_NTF_ROOM_JOIN_FAIL, err);
            }
        } finally {
            RoomManager.I.unlock(player.getUid());
        }
        return null;
    }
}
