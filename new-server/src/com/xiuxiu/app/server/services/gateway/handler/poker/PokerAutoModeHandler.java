package com.xiuxiu.app.server.services.gateway.handler.poker;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerReqAutoMode;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.poker.PokerRoom;
import com.xiuxiu.core.net.message.Handler;

public class PokerAutoModeHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        IRoom room = RoomManager.I.getRoom(player.getRoomId());
        if (room == null) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_POKER_AUTO_MODE_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }
        PCLIPokerReqAutoMode req = (PCLIPokerReqAutoMode) request;
        {
            ((PokerRoom)room).changeAutoModePoker(player,req.auto);
        }
        return null;
    }
}
