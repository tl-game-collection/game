package com.xiuxiu.app.server.services.gateway.handler.poker.cow;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.ERoomState;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.poker.IPokerRoom;
import com.xiuxiu.app.server.room.normal.poker.action.cow.CowReadyAction;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.core.net.message.Handler;

import java.util.Stack;

/**
 * 准备
 */
public class CowHotReadyHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;

        //check Something
        IPokerRoom room = (IPokerRoom) RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_POKER_COW_READY_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }

        if (ERoomState.START != room.getRoomState()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法准备", room, player);
            player.send(CommandId.CLI_NTF_POKER_COW_READY_FAIL, ErrorCode.ROOM_NOT_START);
            return null;
        }
        IPokerPlayer roomPlayer = (IPokerPlayer) room.getRoomPlayer(player.getUid());
        if (null == roomPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里, 无法准备", room, player);
            player.send(CommandId.CLI_NTF_POKER_COW_READY_FAIL, ErrorCode.PLAYER_ROOM_NOT_IN);
            return null;
        }
        Stack<IAction> actions = room.getAction();
        if (actions.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法准备", room, player);
            player.send(CommandId.CLI_NTF_POKER_COW_READY_FAIL, ErrorCode.REQUEST_INVALID);
            return null;
        }

        IAction action = actions.peek();
        if (action instanceof CowReadyAction) {
            ErrorCode err = ((CowReadyAction) action).ready(player.getUid());
            if (ErrorCode.OK == err) {
                room.tick();
                player.send(CommandId.CLI_NTF_POKER_COW_READY_OK, null);
            }else {
                player.send(CommandId.CLI_NTF_POKER_COW_READY_FAIL, err);
            }
        }else {
            Logs.ROOM.warn("%s 本轮不是准备动作, 无法准备", this);
            player.send(CommandId.CLI_NTF_POKER_COW_READY_FAIL, ErrorCode.REQUEST_INVALID);
        }
        return null;
    }
}
