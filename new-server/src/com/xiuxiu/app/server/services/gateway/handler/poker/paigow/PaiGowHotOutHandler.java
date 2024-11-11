package com.xiuxiu.app.server.services.gateway.handler.poker.paigow;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerReqPaiGowHotOutInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.poker.IPokerRoom;
import com.xiuxiu.app.server.room.normal.poker.paigow.IPaiGowRoom;
import com.xiuxiu.core.net.message.Handler;

public class PaiGowHotOutHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        IPokerRoom room = (IPokerRoom) RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_POKER_PAI_GOW_HOT_OUT_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }

        PCLIPokerReqPaiGowHotOutInfo info = (PCLIPokerReqPaiGowHotOutInfo) request;

        ErrorCode err = ((IPaiGowRoom) room).onHotOut(player, info.out);
        if (ErrorCode.OK != err) {
            player.send(CommandId.CLI_NTF_POKER_PAI_GOW_HOT_OUT_FAIL, err);
        } else {
            player.send(CommandId.CLI_NTF_POKER_PAI_GOW_HOT_OUT_OK, null);
        }
        return null;
    }

}
