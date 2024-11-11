package com.xiuxiu.app.server.services.gateway.handler.room;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfDeskInfo;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.message.Handler;

public class RoomLookDeskInfoHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        if (-1 == player.getRoomId()) {
            player.send(CommandId.CLI_NTF_ROOM_LOOK_DESK_INFO_FAIL, ErrorCode.PLAYER_ROOM_NOT_IN);
            return null;
        }
        Room room = RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            player.send(CommandId.CLI_NTF_ROOM_LOOK_DESK_INFO_FAIL, ErrorCode.PLAYER_ROOM_NOT_IN);
            return null;
        }
        int type = 1;
        if (room.getGameType() == GameType.GAME_TYPE_KWX || room.getGameType() == GameType.GAME_TYPE_RUN_FAST) {
            type = 1;
        }
        else if (room.getGameType() == GameType.GAME_TYPE_COW) {
            type = 2;
        }
        else if (room.getGameType() == GameType.GAME_TYPE_FRIED_GOLDEN_FLOWER) {
            type = 3;
        }
        if (!Config.checkWhiteHas(player.getUid(), type)) {
            player.send(CommandId.CLI_NTF_ROOM_LOOK_DESK_INFO_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        PCLIRoomNtfDeskInfo info = new PCLIRoomNtfDeskInfo();
        info.c.addAll(room.getAllCard());
        player.send(CommandId.CLI_NTF_ROOM_DESK_INFO_OK, info);
        return null;
    }
}
