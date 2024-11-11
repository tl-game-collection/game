package com.xiuxiu.app.server.services.gateway.handler.poker;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNetPlayerLookCard;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfCardInfo;
import com.xiuxiu.app.protocol.client.poker.PCLIPokerNtfFGFLookInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.ERoomState;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.poker.IPokerRoom;
import com.xiuxiu.app.server.room.normal.poker.cow.AbstractCowRoom;
import com.xiuxiu.app.server.room.normal.poker.fgf.FGFRoom;
import com.xiuxiu.app.server.room.normal.poker.sg.SGRoom;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.app.server.room.player.poker.FGFPlayer;
import com.xiuxiu.app.server.room.player.poker.SGPlayer;
import com.xiuxiu.app.server.room.player.poker.cow.CowPlayer;
import com.xiuxiu.app.server.room.record.poker.PokerRecord;
import com.xiuxiu.core.net.message.Handler;

import java.util.ArrayList;

/**
 * @auther: yuyunfei
 * @date: 2020/1/9 10:46
 * @comment:
 */
public class PokerLookCardHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        IPokerRoom room = (IPokerRoom) RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_POKER_LOOK_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }
        if (ERoomState.START != room.getRoomState()) {
            Logs.ROOM.warn("%s %s 房间还没开始, 无法看牌", this, player);
            player.send(CommandId.CLI_NTF_POKER_LOOK_FAIL, ErrorCode.ROOM_NOT_START);
            return null;
        }
        IPokerPlayer pokerPlayer = (IPokerPlayer) room.getRoomPlayer(player.getUid());
        if (null == pokerPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里, 无法看牌", this, player);
            player.send(CommandId.CLI_NTF_POKER_LOOK_FAIL, ErrorCode.PLAYER_ROOM_NOT_IN);
            return null;
        }
        if (room instanceof AbstractCowRoom) {//牛牛看牌统计
            ((AbstractCowRoom) room).addLookPlayer(player);
            ((CowPlayer) pokerPlayer).setLook(true);
            PCLIPokerNtfCardInfo ntfCowCardInfo = new PCLIPokerNtfCardInfo();
            CowPlayer cowPlayer = (CowPlayer) pokerPlayer;
            PCLIPokerNetPlayerLookCard resp = new PCLIPokerNetPlayerLookCard();
            if (cowPlayer.getResultCard() != null) {
                ntfCowCardInfo.card.addAll(cowPlayer.getResultCard());
                resp.card.addAll(cowPlayer.getResultCard());
                resp.handCard.addAll(cowPlayer.getHandCard());
            } else {
                ntfCowCardInfo.card.addAll(new ArrayList<>());
                resp.handCard.addAll(new ArrayList<>());
                resp.card.addAll(new ArrayList<>());
            }
            AbstractCowRoom cowRoom=(AbstractCowRoom)room;
            ntfCowCardInfo.cardType = ((CowPlayer) pokerPlayer).getCurCardType().getValue();
            player.send(CommandId.CLI_NTF_POKER_LOOK_OK, ntfCowCardInfo);

            resp.cardType=ntfCowCardInfo.cardType;
            resp.playerUid = player.getUid();
            resp.cardDouble= cowRoom.getMultiple(cowPlayer.getCurCardType());
            room.broadcast2Client(CommandId.CLI_NTF_POKER_PLAYER_LOOK_CARD, resp);
        }else if (room instanceof FGFRoom) {
            if (((FGFPlayer) pokerPlayer).isLook()) {
                Logs.ROOM.warn("%s %s 已经看过牌", this, player);
                player.send(CommandId.CLI_NTF_POKER_LOOK_FAIL, ErrorCode.ROOM_POKER_ALREADY_LOOK);
                return null;
            }
            if (((FGFRoom) room).getCurLoop() <= ((FGFRoom) room).getStuffyLoop()) {
                Logs.ROOM.warn("%s %s 当前轮数小于闷牌轮数", this, player);
                player.send(CommandId.CLI_NTF_POKER_LOOK_FAIL, ErrorCode.ROOM_POKER_FIRST_LOOP_CANT_LOOK);
                return null;
            }
            ((FGFPlayer) pokerPlayer).setLook(true);
            ((PokerRecord) room.getRecord()).addLookCardRecordAction(pokerPlayer.getUid(), pokerPlayer.getHandCard());
            PCLIPokerNtfCardInfo cardInfo = new PCLIPokerNtfCardInfo();
            cardInfo.card.addAll(pokerPlayer.getHandCard());
            cardInfo.cardType = ((FGFPlayer) pokerPlayer).getCurCardType().ordinal();
            player.send(CommandId.CLI_NTF_POKER_LOOK_OK, cardInfo);
            room.broadcast2Client(CommandId.CLI_NTF_POKER_FGF_LOOK, new PCLIPokerNtfFGFLookInfo(pokerPlayer.getUid()));
        }else if (room instanceof SGRoom) {
            if (null == room.getAction() || room.getAction().empty() || room.getRoomState() == ERoomState.STOP){
                return null;
            }

            SGPlayer sgPlayer = (SGPlayer) pokerPlayer;
            ((SGRoom) room).addLookPlayer(player);
            sgPlayer.setLook(true);

            PCLIPokerNtfCardInfo ntfCowCardInfo = new PCLIPokerNtfCardInfo();
            PCLIPokerNetPlayerLookCard resp = new PCLIPokerNetPlayerLookCard();
            if (sgPlayer.getHandCard() != null) {
                ntfCowCardInfo.card.addAll(sgPlayer.getHandCard());
                resp.card.addAll(sgPlayer.getHandCard());
            } else {
                ntfCowCardInfo.card.addAll(new ArrayList<>());
                resp.card.addAll(new ArrayList<>());
            }
            ntfCowCardInfo.cardType = sgPlayer.getCurCardType().getValue();
            ntfCowCardInfo.cardTypeExtra = sgPlayer.getCurCardTypeExtra().ordinal();
            player.send(CommandId.CLI_NTF_POKER_LOOK_OK, ntfCowCardInfo);

            resp.cardType=ntfCowCardInfo.cardType;
            resp.cardTypeExtra = ntfCowCardInfo.cardTypeExtra;
            resp.playerUid = player.getUid();
            resp.cardDouble= ((SGRoom) room).getMultiple(((SGPlayer) pokerPlayer).getCurCardType());
            room.broadcast2Client(CommandId.CLI_NTF_POKER_PLAYER_LOOK_CARD, resp);
        }
        return null;
    }
   
}
