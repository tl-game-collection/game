package com.xiuxiu.app.server.services.gateway.handler.poker;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerReqPlayerPrimula;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.poker.IPokerRoom;
import com.xiuxiu.app.server.room.normal.poker.runFast.RunFastRoom;
import com.xiuxiu.core.net.message.Handler;

/**
 *
 */
public class PokerPlayerPrimulaHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPokerReqPlayerPrimula info = (PCLIPokerReqPlayerPrimula) request;
        IPokerRoom room = (IPokerRoom) RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_POKER_PLAYER_PRIMULA_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }
        ErrorCode err = ((RunFastRoom) room).onPrimula(player, info.primula);
        if (ErrorCode.OK != err) {
            player.send(CommandId.CLI_NTF_POKER_PLAYER_PRIMULA_FAIL, err);
        }
        return null;
    }
}
