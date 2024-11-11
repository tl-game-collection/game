package com.xiuxiu.app.server.services.gateway.handler.room;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.room.PCLIRoomReqNextCardInfo;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.message.Handler;

public class RoomNextCardHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIRoomReqNextCardInfo info = (PCLIRoomReqNextCardInfo) request;
        if (-1 == player.getRoomId()) {
            player.send(CommandId.CLI_NTF_ROOM_NEXT_CARD_FAIL, ErrorCode.PLAYER_ROOM_NOT_IN);
            return null;
        }
        Room room = RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            player.send(CommandId.CLI_NTF_ROOM_NEXT_CARD_FAIL, ErrorCode.PLAYER_ROOM_NOT_IN);
            return null;
        }
        if (!room.isStart()) {
            player.send(CommandId.CLI_NTF_ROOM_NEXT_CARD_FAIL, ErrorCode.ROOM_NOT_START);
            return null;
        }
        int type = 1;
        if (room.getGameType() == GameType.GAME_TYPE_KWX
                || room.getGameType() == GameType.GAME_TYPE_YXMJ
                || room.getGameType() == GameType.GAME_TYPE_HSMJ
                || room.getGameType() == GameType.GAME_TYPE_RUN_FAST
                || room.getGameType() == GameType.GAME_TYPE_HZMJ) {
            type = 1;
        }
        else if (room.getGameType() == GameType.GAME_TYPE_COW) {
            type = 2;
        }
        else if (room.getGameType() == GameType.GAME_TYPE_FRIED_GOLDEN_FLOWER) {
            type = 3;
        }
        else if (room.getGameType() == GameType.GAME_TYPE_SG) {
            type = 4;
        }
        if (!Config.checkWhiteHas(player.getUid(), type)) {
            player.send(CommandId.CLI_NTF_ROOM_NEXT_CARD_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        IRoomPlayer roomPlayer = room.getRoomPlayer(player.getUid());
        if (null == roomPlayer) {
            player.send(CommandId.CLI_NTF_ROOM_NEXT_CARD_FAIL, ErrorCode.PLAYER_ROOM_NOT_IN);
            return null;
        }
        room.replaceHandCard(roomPlayer, info.c);
        player.send(CommandId.CLI_NTF_ROOM_NEXT_CARD_OK, null);

        Logs.ROOM.warn("%s 调用换牌RoomNextCardHandler", player);
        return null;
    }
}
