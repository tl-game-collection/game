package com.xiuxiu.app.server.services.gateway.handler.poker.cow;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.cow.PCLIPokerReqCowRobBankerInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.ERoomState;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.action.cow.CowRobBankerAction;
import com.xiuxiu.app.server.room.normal.poker.IPokerRoom;
import com.xiuxiu.app.server.room.normal.poker.cow.CowRoom;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.app.server.room.player.helper.IArenaRoomPlayerHelper;
import com.xiuxiu.core.net.message.Handler;

/**
 * @auther: yuyunfei
 * @date: 2020/1/8 11:14
 * @comment:
 */
public class PokerCowRobBankerMulHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPokerReqCowRobBankerInfo info = (PCLIPokerReqCowRobBankerInfo) request;
        IPokerRoom room = (IPokerRoom) RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_POKER_COW_ROB_BANKER_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }
        if (ERoomState.START != room.getRoomState()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法抢庄", this, player);
            player.send(CommandId.CLI_NTF_POKER_COW_ROB_BANKER_FAIL, ErrorCode.ROOM_NOT_START);
            return null;
        }
        IPokerPlayer roomPlayer = (IPokerPlayer) room.getRoomPlayer(player.getUid());
        if (null == roomPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里, 无法抢庄", this, player);
            player.send(CommandId.CLI_NTF_POKER_COW_ROB_BANKER_FAIL, ErrorCode.PLAYER_ROOM_NOT_IN);
            return null;
        }
        if (roomPlayer.isGuest()) {
            Logs.ROOM.warn("%s %s 观察者, 无法下注", this, player);
            player.send(CommandId.CLI_NTF_POKER_COW_ROB_BANKER_FAIL, ErrorCode.REQUEST_INVALID);
            return null;
        }
        if (info.mul < 0 || (info.mul != 0 && info.mul > ((CowRoom) room).getCowInfo().getRobBankerMul())) {
            Logs.ROOM.warn("%s %s 抢庄倍数不对, rule:robBankerMul:%d player mul:%d", this, player, ((CowRoom)room).getCowInfo().getRobBankerMul(), info.mul);
            player.send(CommandId.CLI_NTF_POKER_COW_ROB_BANKER_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        if (room.getAction().isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法抢庄", this, player);
            player.send(CommandId.CLI_NTF_POKER_COW_ROB_BANKER_FAIL, ErrorCode.REQUEST_INVALID);
            return null;
        }
        IArenaRoomPlayerHelper roomPlayerHelper = (IArenaRoomPlayerHelper) roomPlayer.getRoomPlayerHelper();
        if (null != roomPlayerHelper){
            if (!roomPlayerHelper.checkEnoughGold(((CowRoom) room).getCowInfo().getRobLessAreanValue() * 100) && info.mul != 0){
                Logs.ROOM.warn("%s %s 竞技值不够, 无法抢庄", this, player);
                ((CowRoom) room).setRobBankerFail(player);
                player.send(CommandId.CLI_NTF_POKER_COW_ROB_BANKER_FAIL, ErrorCode.ARENA_LESS_THAN_ROB_BANKER);
                return null;
            }
        }
        IAction action = room.getAction().peek();
        if (action instanceof CowRobBankerAction) {
            ErrorCode err = ((CowRobBankerAction) action).selectRobBaker(player.getUid(), info.mul);
            if (ErrorCode.OK == err) {
                roomPlayer.setScore(Score.POKER_COW_ROB_BANKER_MUL, info.mul, false);
                room.tick();
                player.send(CommandId.CLI_NTF_POKER_COW_ROB_BANKER_OK, null);
            } else {
                player.send(CommandId.CLI_NTF_POKER_COW_ROB_BANKER_FAIL, err);
            }
        }
        return null;
    }
}
