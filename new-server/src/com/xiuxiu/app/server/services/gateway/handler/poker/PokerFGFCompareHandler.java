package com.xiuxiu.app.server.services.gateway.handler.poker;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerReqFGFCompareInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.poker.fgf.IFGFRoom;
import com.xiuxiu.core.net.message.Handler;

public class PokerFGFCompareHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPokerReqFGFCompareInfo info = (PCLIPokerReqFGFCompareInfo) request;
        IFGFRoom room = (IFGFRoom) RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_POKER_FGF_COMPARE_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }
        ErrorCode err = room.compare(player, info.comparePlayerUid);
        if (ErrorCode.OK != err) {
            player.send(CommandId.CLI_NTF_POKER_FGF_COMPARE_FAIL, err);
        }
        return null;
    }
}
