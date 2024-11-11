package com.xiuxiu.app.server.services.gateway.handler.poker.thirteen;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerReqThirteenTakeInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.poker.IPokerRoom;
import com.xiuxiu.app.server.room.normal.poker.thirteen.ThirteenRoom;
import com.xiuxiu.core.net.message.Handler;

public class PokerThirteenTakeHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPokerReqThirteenTakeInfo info = (PCLIPokerReqThirteenTakeInfo) request;
        IPokerRoom room = (IPokerRoom) RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_POKER_THIRTEEN_TAKE_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }
        if (null == info) {
            Logs.ROOM.warn("%s 无效数据", player);
            player.send(CommandId.CLI_NTF_POKER_THIRTEEN_TAKE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }

        ErrorCode err = ((ThirteenRoom) room).thirteenTake(player, info.cards, info.monsterType, info.headType, info.mediumType, info.tailType);
        if (ErrorCode.OK != err) {
            player.send(CommandId.CLI_NTF_POKER_THIRTEEN_TAKE_FAIL, err);
        } else {
            Logs.ROOM.debug("PokerThirteenTakeHandler is ok");
        }
        return null;
    }
}
