package com.xiuxiu.app.server.services.gateway.handler.poker.cow;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCIPokerNtfGiveBankerInfo;
import com.xiuxiu.app.protocol.client.poker.PCIPokerReqGiveBankerInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.ERoomType;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.poker.IPokerRoom;
import com.xiuxiu.app.server.room.normal.poker.cow.CowRoom;
import com.xiuxiu.app.server.room.normal.poker.cow.ECowPlayTypes;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.core.net.message.Handler;

/**
 * @auther: yuyunfei
 * @date: 2020/1/8 14:37
 * @comment:
 */
public class PokerCowGiveBankerHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCIPokerReqGiveBankerInfo info = (PCIPokerReqGiveBankerInfo) request;
        IPokerRoom room = (IPokerRoom) RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_POKER_COW_GIVE_BANKER_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }
        IPokerPlayer pokerPlayer = (IPokerPlayer) room.getRoomPlayer(player.getUid());
        IPokerPlayer pokerPlayer1 = (IPokerPlayer) room.getRoomPlayer(info.getBankerPlayerID);
        if (null == pokerPlayer || null == pokerPlayer1) {
            Logs.ROOM.warn("%s %s 不在房间里, 无法操作", this, player);
            player.send(CommandId.CLI_NTF_POKER_COW_GIVE_BANKER_FAIL, ErrorCode.PLAYER_ROOM_NOT_IN);
            return null;
        }
        if (room.getRoomType() != ERoomType.BOX) {
            Logs.ROOM.warn("%s %s 不在包房里, 无法操作", this, player);
            player.send(CommandId.CLI_NTF_POKER_COW_GIVE_BANKER_FAIL, ErrorCode.PLAYER_ROOM_NOT_IN);
            return null;
        }
        if (!(ECowPlayTypes.OVERLORD_BANKER.ordinal() == ((CowRoom) room).getCowInfo().getBankerType() || (ECowPlayTypes.HS_ROB_BANKER.ordinal() == ((CowRoom) room).getCowInfo().getBankerType() && 1 == ((CowRoom) room).getCowInfo().getPushBankerType()))) {
            player.send(CommandId.CLI_NTF_POKER_COW_GIVE_BANKER_FAIL, ErrorCode.PLAYER_ROOM_NOT_IN);
            return null;
        }
        if (room.getCurBureau() > 0) {
            Logs.ROOM.debug("%s 房间已经在进行中", this);
            player.send(CommandId.CLI_NTF_POKER_COW_GIVE_BANKER_FAIL, ErrorCode.REQUEST_INVALID);
            return null;
        }
        ((CowRoom) room).onBankerToPlayer(info.getBankerPlayerID);
        PCIPokerNtfGiveBankerInfo bankerInfo = new PCIPokerNtfGiveBankerInfo();
        bankerInfo.getBankerPlayerID = info.getBankerPlayerID;
        player.send(CommandId.CLI_NTF_POKER_COW_GIVE_BANKER_OK, bankerInfo);
        return null;
    }
}
