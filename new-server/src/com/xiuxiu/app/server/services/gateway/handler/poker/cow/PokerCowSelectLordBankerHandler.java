package com.xiuxiu.app.server.services.gateway.handler.poker.cow;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.cow.PCLIPokerReqCowSelectBankerInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.ERoomState;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.action.cow.CowLordBankerAction;
import com.xiuxiu.app.server.room.normal.poker.IPokerRoom;
import com.xiuxiu.core.net.message.Handler;

/**
 * @auther: yuyunfei
 * @date: 2020/1/8 14:45
 * @comment:
 */
public class PokerCowSelectLordBankerHandler implements Handler {

    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPokerReqCowSelectBankerInfo info = (PCLIPokerReqCowSelectBankerInfo) request;
        IPokerRoom room = (IPokerRoom) RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_POKER_COW_LORD_BANKER_STATE_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }
        if (ERoomState.START != room.getRoomState()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法操作", this, player);
            player.send(CommandId.CLI_NTF_POKER_COW_LORD_BANKER_STATE_FAIL, ErrorCode.ROOM_NOT_START);
            return null;
        }
        IAction action = room.getAction().peek();
        if (action instanceof CowLordBankerAction) {
            ErrorCode err = ((CowLordBankerAction) action).setSelectBanker(player.getUid(), info.selectState);
            if (ErrorCode.OK == err) {
                room.tick();
                player.send(CommandId.CLI_NTF_POKER_COW_LORD_BANKER_STATE_OK, null);
            }
            player.send(CommandId.CLI_NTF_POKER_COW_LORD_BANKER_STATE_FAIL, err);
        }
        return null;
    }
}
