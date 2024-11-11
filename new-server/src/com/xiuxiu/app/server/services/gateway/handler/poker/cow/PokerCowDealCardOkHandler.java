package com.xiuxiu.app.server.services.gateway.handler.poker.cow;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.poker.IPokerRoom;
import com.xiuxiu.app.server.room.normal.poker.cow.CowRoom;
import com.xiuxiu.core.net.message.Handler;

/**
 * @auther: yuyunfei
 * @date: 2020/1/8 14:08
 * @comment:
 */
public class PokerCowDealCardOkHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        IPokerRoom room = (IPokerRoom) RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_POKER_COW_REBET_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }
        if (((CowRoom) room).getCowInfo().isSendRobBanker()) {
            player.send(CommandId.CLI_NTF_POKER_COW_DEALCARD_OVER_FAIL, ErrorCode.REQUEST_INVALID);
            return null;
        }
        ((CowRoom) room).getCowInfo().addDealCardOkCnt();
        if (((CowRoom) room).getCowInfo().getDealCardOkCnt() == room.getCurPlayerCnt()) {
            ((CowRoom) room).showCardRobBanker();
        }
        player.send(CommandId.CLI_NTF_POKER_COW_DEALCARD_OVER_OK, null);
        return null;
    }
}
