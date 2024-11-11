package com.xiuxiu.app.server.services.gateway.handler.poker.paigow;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfPaiGowRebetMulInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.poker.paigow.IPaiGowRoom;
import com.xiuxiu.core.net.message.Handler;

public class PokerPaiGowRobBankerHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        IPaiGowRoom room = (IPaiGowRoom) RoomManager.I.getRoom(player.getRoomId());
        PCLIPokerNtfPaiGowRebetMulInfo info = (PCLIPokerNtfPaiGowRebetMulInfo) request;
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_POKER_COW_ROB_BANKER_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }
        ErrorCode err = room.onRobBank(player, info.robMul);
        if (ErrorCode.OK != err) {
            player.send(CommandId.CLI_NTF_POKER_COW_ROB_BANKER_FAIL, err);
        }
        return null;
    }
}
