package com.xiuxiu.app.server.services.gateway.handler.poker.paigow;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.IPlayer;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.ERoomState;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.poker.IPokerRoom;
import com.xiuxiu.app.server.room.normal.poker.action.paigow.PaiGowReadyAction;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.core.net.message.Handler;

import java.util.Stack;

/**
 * 加锅牌九准备
 */
public class PaiGowHotReadyHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        IPokerRoom room = (IPokerRoom) RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_POKER_PAI_GOW_READY_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }

        ErrorCode err = onReady(player, room);
        if (ErrorCode.OK != err) {
            player.send(CommandId.CLI_NTF_POKER_PAI_GOW_READY_FAIL, err);
        } else {
            player.send(CommandId.CLI_NTF_POKER_PAI_GOW_READY_OK, null);
        }
        return null;
    }

    /**
     * 准备
     * @param player
     * @param room
     * @return
     */
    private ErrorCode onReady(IPlayer player, IPokerRoom room) {
        if (ERoomState.START != room.getRoomState()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法准备", room, player);
            return ErrorCode.ROOM_NOT_START;
        }
        IPokerPlayer roomPlayer = (IPokerPlayer) room.getRoomPlayer(player.getUid());
        if (null == roomPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里, 无法准备", room, player);
            return ErrorCode.PLAYER_ROOM_NOT_IN;
        }
        Stack<IAction> actions = room.getAction();
        if (actions.isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法准备", room, player);
            return ErrorCode.REQUEST_INVALID;
        }
        Logs.ROOM.debug("%s %s  onReady ", room, player);

        IAction action = actions.peek();
        if (action instanceof PaiGowReadyAction) {
            ErrorCode err = ((PaiGowReadyAction) action).ready(player.getUid());
            if (ErrorCode.OK == err) {
                room.tick();
            }
            return err;
        }
        Logs.ROOM.warn("%s 本轮不是准备动作, 无法准备", this);
        return ErrorCode.REQUEST_INVALID;
    }
}
