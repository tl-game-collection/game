package com.xiuxiu.app.server.services.gateway.handler.room;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.room.PCLIRoomReqChangeSeat;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.core.net.message.Handler;

public class RoomChangeSeat implements Handler {

    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIRoomReqChangeSeat info = (PCLIRoomReqChangeSeat) request;
        if (!RoomManager.I.lock(player.getUid())) {
            player.send(CommandId.CLI_NTF_ROOM_CHANGE_SEATS_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            ErrorCode err = RoomManager.I.changeSeat(player, info.seatIndex);
            if (ErrorCode.OK != err) {
                player.send(CommandId.CLI_NTF_ROOM_CHANGE_SEATS_FAIL, err);
            }
        } finally {
            RoomManager.I.unlock(player.getUid());
        }
        return null;
    }
}

