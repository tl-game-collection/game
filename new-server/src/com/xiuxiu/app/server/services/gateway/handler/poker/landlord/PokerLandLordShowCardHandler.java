package com.xiuxiu.app.server.services.gateway.handler.poker.landlord;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.poker.IPokerRoom;
import com.xiuxiu.app.server.room.normal.poker.landLord.LandLordRoom;
import com.xiuxiu.core.net.message.Handler;

public class PokerLandLordShowCardHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        IPokerRoom room = (IPokerRoom) RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_POKER_LAND_LORD_SHOW_CARD_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }

        ErrorCode err = ((LandLordRoom) room).showCard(player);
        if (ErrorCode.OK != err) {
            player.send(CommandId.CLI_NTF_POKER_LAND_LORD_SHOW_CARD_FAIL, err);
        }
        return null;
    }
}
