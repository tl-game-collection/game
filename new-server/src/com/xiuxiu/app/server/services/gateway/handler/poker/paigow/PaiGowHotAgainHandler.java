package com.xiuxiu.app.server.services.gateway.handler.poker.paigow;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerReqPaiGowHotAgainInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.poker.IPokerRoom;
import com.xiuxiu.app.server.room.normal.poker.paigow.IPaiGowRoom;
import com.xiuxiu.core.net.message.Handler;

public class PaiGowHotAgainHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        IPokerRoom room = (IPokerRoom) RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_POKER_PAI_GOW_HOT_AGAIN_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }

        PCLIPokerReqPaiGowHotAgainInfo info = (PCLIPokerReqPaiGowHotAgainInfo) request;

        ErrorCode err = ((IPaiGowRoom) room).onHotAgain(player, info.again, 0);
        if (ErrorCode.OK != err) {
            player.send(CommandId.CLI_NTF_POKER_PAI_GOW_HOT_AGAIN_FAIL, err);
        } else {
            player.send(CommandId.CLI_NTF_POKER_PAI_GOW_HOT_AGAIN_OK, null);
        }
        return null;
    }
}
