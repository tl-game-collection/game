package com.xiuxiu.app.server.services.gateway.handler.poker;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerReqTakeInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.poker.IPokerRoom;
import com.xiuxiu.core.net.message.Handler;

public class PokerTakeHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPokerReqTakeInfo info = (PCLIPokerReqTakeInfo) request;
        IPokerRoom room = (IPokerRoom) RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_POKER_TAKE_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }
        if (null == info) {
            Logs.ROOM.warn("%s 无效数据", player);
            player.send(CommandId.CLI_NTF_POKER_TAKE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        ErrorCode err = room.take(player, info.cards, info.laiziCards, info.cardType);
        if (ErrorCode.OK != err) {
            player.send(CommandId.CLI_NTF_POKER_TAKE_FAIL, err);
        }
        return null;
    }
}
