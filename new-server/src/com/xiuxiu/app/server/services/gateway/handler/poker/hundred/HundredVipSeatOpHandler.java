package com.xiuxiu.app.server.services.gateway.handler.poker.hundred;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredReqVipSeatOp;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.handle.impl.hundred.IHundredHandle;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.message.Handler;

/**
 * 请求VIP坐下/站起
 */
public class HundredVipSeatOpHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIHundredReqVipSeatOp info = (PCLIHundredReqVipSeatOp) request;
        Room room = RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ARENA.warn("%s 百人场不存在, 无法下庄 info:%s", player, info);
            player.send(CommandId.CLI_NTF_ARENA_HUNDRED_VIP_SEAT_OP_FAIL, ErrorCode.ARENA_NOT_EXISTS);
            return null;
        }
        IHundredHandle hundredHandle = (IHundredHandle)room.getRoomHandle();
        ErrorCode err = hundredHandle.vipSeatOp(player.getUid(), info.index);
        if (ErrorCode.OK == err) {
            player.send(CommandId.CLI_NTF_ARENA_HUNDRED_VIP_SEAT_OP_OK, null);
        } else {
            player.send(CommandId.CLI_NTF_ARENA_HUNDRED_VIP_SEAT_OP_FAIL, err);
        }
        return null;
    }
}
