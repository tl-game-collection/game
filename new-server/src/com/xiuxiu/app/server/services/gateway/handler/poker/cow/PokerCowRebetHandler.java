package com.xiuxiu.app.server.services.gateway.handler.poker.cow;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.cow.PCLIPokerReqCowReBetInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.constant.EBoxType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.ERoomState;
import com.xiuxiu.app.server.room.ERoomType;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.Score;
import com.xiuxiu.app.server.room.handle.IBoxRoomHandle;
import com.xiuxiu.app.server.room.normal.action.IAction;
import com.xiuxiu.app.server.room.normal.action.cow.CowReBetAction;
import com.xiuxiu.app.server.room.normal.poker.IPokerRoom;
import com.xiuxiu.app.server.room.normal.poker.cow.CowHotRoom;
import com.xiuxiu.app.server.room.normal.poker.cow.CowRoom;
import com.xiuxiu.app.server.room.normal.poker.cow.ECowPlayTypes;
import com.xiuxiu.app.server.room.normal.poker.cow.ICowRoom;
import com.xiuxiu.app.server.room.player.IPokerPlayer;
import com.xiuxiu.app.server.room.player.helper.IArenaRoomPlayerHelper;
import com.xiuxiu.core.net.message.Handler;

/**
 * @auther: yuyunfei
 * @date: 2020/1/8 14:29
 * @comment:
 */
public class PokerCowRebetHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPokerReqCowReBetInfo info = (PCLIPokerReqCowReBetInfo) request;
        int roomId = player.getRoomId();
        IPokerRoom room = (IPokerRoom) RoomManager.I.getRoom(roomId);
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_POKER_COW_REBET_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }
        if (ERoomState.START != room.getRoomState()) {
            Logs.ROOM.warn("%s %s 房间已经结束, 无法下注", this, player);
            player.send(CommandId.CLI_NTF_POKER_COW_REBET_FAIL, ErrorCode.ROOM_NOT_START);
            return null;
        }
        long uid = player.getUid();
        IPokerPlayer roomPlayer = (IPokerPlayer) room.getRoomPlayer(uid);
        if (null == roomPlayer) {
            Logs.ROOM.warn("%s %s 不在房间里, 无法下注", this, player);
            player.send(CommandId.CLI_NTF_POKER_COW_REBET_FAIL, ErrorCode.PLAYER_ROOM_NOT_IN);
            return null;
        }
        if (roomPlayer.isGuest()) {
            Logs.ROOM.warn("%s %s 观察者, 无法下注", this, player);
            player.send(CommandId.CLI_NTF_POKER_COW_REBET_FAIL, ErrorCode.REQUEST_INVALID);
            return null;
        }
        if (room.getAction().isEmpty()) {
            Logs.ROOM.warn("%s %s 没有动作, 无法下注", this, player);
            player.send(CommandId.CLI_NTF_POKER_COW_REBET_FAIL, ErrorCode.REQUEST_INVALID);
            return null;
        }
        //庄家索引
        int bankerIndex = room.getBankerIndex();
        //牛牛抢庄类型
        int eCowPlayTypes = ECowPlayTypes.COMMON_PLAYING.ordinal();
        //1.斗公牛 2.明牌抢庄
        int gameSubType = room.getGameSubType();
        //庄类型
        int bankerType = 0;
        if(gameSubType==1) {
        	CowHotRoom cowRoom = (CowHotRoom)room;
        	bankerType = cowRoom.getCowInfo().getBankerType();
        }else if(gameSubType==2) {
        	CowRoom cowRoom = (CowRoom)room;
        	bankerType = cowRoom.getCowInfo().getBankerType();
        }
        // 通比玩法，没有庄家，都可以下注
        if (roomPlayer.getIndex() == bankerIndex && eCowPlayTypes != bankerType) {
            Logs.ROOM.warn("%s %s 庄家无法下注, 无法下注", this, player);
            player.send(CommandId.CLI_NTF_POKER_COW_REBET_FAIL, ErrorCode.ROOM_POKER_COW_BANKER_NOT_REBET);
            return null;
        }
        //1分代表1分钱,10分代表1毛钱,100分代表1元钱
        //0.1下1毛
        //下注分0.1->1->10(0.1:前端显示值;1:前端传入服务端的值;10实际值);
        int rebet = info.rebet;
        if (rebet < 1) {
            Logs.ROOM.warn("%s %s 下注金额不对, 无法下注", this, player);
            player.send(CommandId.CLI_NTF_POKER_COW_REBET_FAIL, ErrorCode.REQUEST_INVALID);
            return null;
        }
        IArenaRoomPlayerHelper roomPlayerHelper = (IArenaRoomPlayerHelper) roomPlayer.getRoomPlayerHelper();
        ICowRoom iCowRoom = (ICowRoom) room;
        //房间类型
        ERoomType roomType = room.getRoomType();
        //包厢类型
        EBoxType boxType = ((IBoxRoomHandle) room.getRoomHandle()).getBoxType();
        //判断房间类型是否是包厢房,判断包厢类型是否是竞技场包厢
        if (ERoomType.BOX == roomType && boxType == EBoxType.ARENA) {
        	//下注玩家身上金币
        	long gold = iCowRoom.getPlayerGold(uid);
        	//下注玩家身上金币转换成游戏分score = gold
        	int score = iCowRoom.getExchangeGoldForScore(gold);
        	int bankertype = iCowRoom.getCowInfo().getBankerType();
        	//端火锅
        	int hotPot = ECowPlayTypes.HOT_POT.ordinal();
            //比较下注玩家身上竞技值和下注值，如果下注玩家身上竞技值小于下注值，提示竞技值不足;
        	if (null == roomPlayerHelper || (score < rebet && bankertype == hotPot)) {
                Logs.ROOM.warn("%s %s 竞技值不足, 无法下注", this, player);
                player.send(CommandId.CLI_NTF_POKER_COW_REBET_FAIL, ErrorCode.ARENA_LESS_THAN_MIN_VALUE);
                return null;
            }
        }
        IAction action = room.getAction().peek();
        if (action instanceof CowReBetAction) {
            ErrorCode err = ((CowReBetAction) action).rebet(player.getUid(), rebet);
            if (ErrorCode.OK == err) {
            	//放入房间下注玩家下注值
                roomPlayer.setScore(Score.POKER_COW_REBET, rebet, false);
                room.tick();
                player.send(CommandId.CLI_NTF_POKER_COW_REBET_OK, null);
            } else {
                player.send(CommandId.CLI_NTF_POKER_COW_REBET_FAIL, err);
            }
        }
        return null;
    }
}
