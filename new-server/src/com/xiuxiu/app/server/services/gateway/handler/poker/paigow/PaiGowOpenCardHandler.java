package com.xiuxiu.app.server.services.gateway.handler.poker.paigow;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerReqPaiGowOpenInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.poker.paigow.IPaiGowRoom;
import com.xiuxiu.core.net.message.Handler;

/**
 * 牌九开牌
 */
public class PaiGowOpenCardHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPokerReqPaiGowOpenInfo info = (PCLIPokerReqPaiGowOpenInfo) request;
        IPaiGowRoom room = (IPaiGowRoom) RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_POKER_PAI_GOW_OPEN_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }

        ErrorCode err = room.onOpenCard(player, info.card1, info.card2);
        if (ErrorCode.OK != err) {
            player.send(CommandId.CLI_NTF_POKER_PAI_GOW_OPEN_FAIL, err);
        }else{
            player.send(CommandId.CLI_NTF_POKER_PAI_GOW_OPEN_OK, err);
        }
        return null;
    }
}
