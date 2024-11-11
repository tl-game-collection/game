package com.xiuxiu.app.server.services.gateway.handler.poker.landlord;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerReqLandLordKickSelect;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.poker.landLord.LandLordRoom;
import com.xiuxiu.core.net.message.Handler;

public class PokerLandLordKickSelectHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPokerReqLandLordKickSelect info = (PCLIPokerReqLandLordKickSelect) request;
        LandLordRoom room = (LandLordRoom) RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_POKER_LAND_LORD_KICK_SELECT_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }
        if (null == info) {
            Logs.ROOM.warn("%s 无效数据", player);
            player.send(CommandId.CLI_NTF_POKER_LAND_LORD_KICK_SELECT_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }

        room.kickSelect(player, info.type);
        return null;
    }
}
