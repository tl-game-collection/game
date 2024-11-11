package com.xiuxiu.app.server.services.gateway.handler.poker.cow;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.cow.PCLIPokerReqCowHotOutInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.poker.IPokerRoom;
import com.xiuxiu.app.server.room.normal.poker.cow.CowHotRoom;
import com.xiuxiu.core.net.message.Handler;

public class CowHotOutHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        IPokerRoom room = (IPokerRoom) RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_POKER_COW_OUT_HOT_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }

        PCLIPokerReqCowHotOutInfo info = (PCLIPokerReqCowHotOutInfo) request;

        ErrorCode err = ((CowHotRoom) room).onHotOut(player, info.out == 1);
        if (ErrorCode.OK != err) {
            player.send(CommandId.CLI_NTF_POKER_COW_OUT_HOT_FAIL, err);
        } else {
            player.send(CommandId.CLI_NTF_POKER_COW_OUT_HOT_OK, null);
        }
        return null;
    }

}
