package com.xiuxiu.app.server.services.gateway.handler.poker.hundred;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredNtfDownBanker;
import com.xiuxiu.app.protocol.client.hundred.PCLIHundredReqDownBanker;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.handle.impl.hundred.IHundredHandle;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.message.Handler;

/**
 * 请求下庄
 */
public class HundredDownBankerHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIHundredReqDownBanker info = (PCLIHundredReqDownBanker) request;
        Room room = RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ARENA.warn("%s 百人场不存在, 无法下庄 info:%s", player, info);
            player.send(CommandId.CLI_NTF_ARENA_HUNDRED_DOWN_BANKER_FAIL, ErrorCode.ARENA_NOT_EXISTS);
            return null;
        }
        IHundredHandle hundredHandle = (IHundredHandle)room.getRoomHandle();
        ErrorCode err = hundredHandle.downBanker(player, info.bankerUid);
        if (ErrorCode.OK == err) {
            PCLIHundredNtfDownBanker downBanker = new PCLIHundredNtfDownBanker();
            downBanker.boxId = info.boxId;
            downBanker.bankerUid = info.bankerUid;
            player.send(CommandId.CLI_NTF_ARENA_HUNDRED_DOWN_BANKER_OK, downBanker);
        } else {
            player.send(CommandId.CLI_NTF_ARENA_HUNDRED_DOWN_BANKER_FAIL, err);
        }
        return null;
    }
}
