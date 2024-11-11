package com.xiuxiu.app.server.services.gateway.handler.poker.cow;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.cow.PCLIPokerReqHotCowLeaveBankerInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.poker.IPokerRoom;
import com.xiuxiu.app.server.room.normal.poker.cow.CowRoom;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.app.server.room.player.poker.cow.CowPlayer;
import com.xiuxiu.core.net.message.Handler;

/**
 * @auther: yuyunfei
 * @date: 2020/1/8 14:13
 * @comment:
 */
public class PokerHotCowLeaveBankerHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPokerReqHotCowLeaveBankerInfo info = (PCLIPokerReqHotCowLeaveBankerInfo) request;
        IPokerRoom room = (IPokerRoom) RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_POKER_COW_LORD_BANKER_STATE_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }
        IPokerPlayer roomPlayer = (IPokerPlayer) room.getRoomPlayer(player.getUid());
        if (null == roomPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里,", this, player);
            player.send(CommandId.CLI_NTF_POKER_COW_LORD_BANKER_STATE_FAIL, ErrorCode.PLAYER_ROOM_NOT_IN);
            return null;
        }
        if (((CowRoom) room).getCowInfo().getCurHotBankerLoop() < ((CowRoom) room).getCowInfo().getHotLessLoop()) {
            Logs.ROOM.warn("%s %s 当前庄不能进行抽庄,", this, player);
            player.send(CommandId.CLI_NTF_POKER_COW_LORD_BANKER_STATE_FAIL, ErrorCode.REQUEST_INVALID);
            return null;
        }
        if (((CowRoom) room).getCowInfo().getCurHotDeskNote() <= 0) {
            Logs.ROOM.warn("%s %s 当前庄不能进行抽庄,", this, player);
            player.send(CommandId.CLI_NTF_POKER_COW_LORD_BANKER_STATE_FAIL, ErrorCode.REQUEST_INVALID);
            return null;
        }
        CowPlayer bankerPlayer = (CowPlayer) room.getRoomPlayer(room.getBankerIndex());
        if (bankerPlayer.getUid() != info.playerUid) {
            Logs.ROOM.warn("%s %s 请求玩家不是庄家,", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        if (!((CowRoom) room).isCheckAgain()) {
            Logs.ROOM.warn("%s %s 已经结束,", this, player);
            return ErrorCode.REQUEST_INVALID;
        }
        //((CowRoom) room).onHotBankerLeave(bankerPlayer);
        player.send(CommandId.CLI_NTF_POKER_COW_LORD_BANKER_STATE_OK, null);
        return null;
    }
}
