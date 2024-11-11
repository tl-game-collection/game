package com.xiuxiu.app.server.services.gateway.handler.poker;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class PokerRobRedEnvelopeHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        player.send(CommandId.ERROR, ErrorCode.DISCARD_PROTOCOL);
        //IPokerRoom room = (IPokerRoom) RoomManager.I.getRoom(player.getRoomId());
        //if (null == room) {
        //    Logs.ROOM.warn("%s 不在房间里", player);
        //    player.send(CommandId.CLI_NTF_POKER_RE_ROB_RED_FAIL, ErrorCode.ROOM_NOT_EXISTS);
        //    return null;
        //}

        //ErrorCode err = ((RedEnvelopeRoom) room).robRedEnvelope(player);
        //if (ErrorCode.OK != err) {
        //    player.send(CommandId.CLI_NTF_POKER_RE_ROB_RED_FAIL, err);
        //} else {
        //    Logs.ROOM.debug("PokerRobRedEnvelopeHandler is ok");
        //}
        return null;
    }
}
