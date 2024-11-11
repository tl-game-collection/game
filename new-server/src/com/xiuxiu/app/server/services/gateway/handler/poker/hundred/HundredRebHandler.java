package com.xiuxiu.app.server.services.gateway.handler.poker.hundred;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredReqReb;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.handle.impl.hundred.IHundredHandle;
import com.xiuxiu.app.server.room.normal.Hundred.EHundredArenaRebType;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.message.Handler;

/**
 * 请求下注
 */
public class HundredRebHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIHundredReqReb info = (PCLIHundredReqReb) request;
        Room room = RoomManager.I.getRoom(player.getRoomId());
        //房间不存在
        if (null == room) {
            player.send(CommandId.CLI_NTF_ARENA_HUNDRED_REB_FAIL, ErrorCode.ARENA_NOT_EXISTS);
            return null;
        }
        if (info.type < 0 || info.type >= EHundredArenaRebType.values().length) {
            Logs.ARENA.warn("%s 无效下注类型, 无法下注 info:%s", player, info);
            player.send(CommandId.CLI_NTF_ARENA_HUNDRED_REB_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        IHundredHandle hundredHandle = (IHundredHandle)room.getRoomHandle();
        ErrorCode err = hundredHandle.reb(player, info.index, info.value, EHundredArenaRebType.values()[info.type]);
        if (ErrorCode.OK == err) {
            player.send(CommandId.CLI_NTF_ARENA_HUNDRED_REB_OK, null);
        } else {
            player.send(CommandId.CLI_NTF_ARENA_HUNDRED_REB_FAIL, err);
        }
        return null;
    }
}
