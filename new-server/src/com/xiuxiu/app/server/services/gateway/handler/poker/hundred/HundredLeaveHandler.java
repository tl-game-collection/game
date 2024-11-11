package com.xiuxiu.app.server.services.gateway.handler.poker.hundred;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredReqLeave;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.handle.IRoomHandle;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.message.Handler;

/**
 * 请求离开百人场
 */
public class HundredLeaveHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIHundredReqLeave info = (PCLIHundredReqLeave) request;
        Room room = RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ARENA.warn("%s 百人场不存在, 无法离开 info:%s", player, info);
            player.send(CommandId.CLI_NTF_ARENA_HUNDRED_LEAVE_FAIL, ErrorCode.ARENA_NOT_EXISTS);
            return null;
        }
        IRoomHandle roomHandle = room.getRoomHandle();
        ErrorCode err = roomHandle.leave(player);
        if (err == ErrorCode.OK) {
            player.send(CommandId.CLI_NTF_ARENA_HUNDRED_LEAVE_OK, null);
        } else {
            player.send(CommandId.CLI_NTF_ARENA_HUNDRED_LEAVE_FAIL, err);
        }

        return null;
    }
}
