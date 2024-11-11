package com.xiuxiu.app.server.services.gateway.handler.room;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfLeaveStateInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.message.Handler;

public class RoomLeaveV2Handler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        if (!RoomManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s playerUid:%d 正在操作", player, player.getUid());
            player.send(CommandId.CLI_NTF_ROOM_JOIN_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            ErrorCode err = RoomManager.I.leave(player);
            if (ErrorCode.OK != err && ErrorCode.ROOM_ALREADY_START != err && ErrorCode.ROOM_LEAVE != err && ErrorCode.ROOM_LEAVE_REPORT != err && ErrorCode.ROOM_LEAVE_LIMIT != err) {
                player.send(CommandId.CLI_NTF_ROOM_LEAVE_V2_FAIL, err);
            } else {
                int state = 1;
                if (ErrorCode.ROOM_ALREADY_START == err) {
                    state = 3;
                } else if (ErrorCode.ROOM_LEAVE == err) {
                    state = 2;
                } else if (ErrorCode.ROOM_LEAVE_REPORT == err) {
                    state = 4;
                } else if (ErrorCode.ROOM_LEAVE_LIMIT == err) {
                    state = 5;
                }
                PCLIRoomNtfLeaveStateInfo info = new PCLIRoomNtfLeaveStateInfo();
                info.state = state;
                player.send(CommandId.CLI_NTF_ROOM_LEAVE_V2_OK, info);
            }
        } finally {
            RoomManager.I.unlock(player.getUid());
        }
        return null;
    }
}
