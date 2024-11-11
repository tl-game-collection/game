package com.xiuxiu.app.server.services.gateway.handler.poker.cow;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiuxiu.algorithm.poker.EPokerCardType;
import com.xiuxiu.algorithm.poker.PokerUtil;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.poker.IPokerRoom;
import com.xiuxiu.app.server.room.normal.poker.cow.AbstractCowRoom;
import com.xiuxiu.app.server.room.player.poker.cow.CowPlayer;
import com.xiuxiu.core.net.message.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @auther: yuyunfei
 * @date: 2020/1/8 14:53
 * @comment:
 */
public class PokerCowGetRobotHandler  implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        IPokerRoom room = (IPokerRoom) RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_POKER_COW_READY_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }
        AbstractCowRoom cowRoom = (AbstractCowRoom) room;
        JSONArray array = new JSONArray();
        for (int i = 0; i < cowRoom.getAllPlayer().length; ++i) {
            CowPlayer pl = (CowPlayer) room.getAllPlayer()[i];
            if (null == pl || pl.isGuest()) {
                continue;
            }
            JSONObject js = new JSONObject();
            //排序
            PokerUtil.sortByCow(pl.getHandCard());
            List<Byte> result = new ArrayList<>(5);
            EPokerCardType cardType = EPokerCardType.COW_NONE;
            double cardValue = -1;
            do {
                cardValue = PokerUtil.isCowWithTheFlower(pl.getHandCard(), result);
                if (-1 != cardValue) {
                    cardType = EPokerCardType.COW_WITH_THE_FLOWER;
                    break;
                }
                cardValue = PokerUtil.isCowDragon(pl.getHandCard(), result);
                if (-1 != cardValue) {
                    cardType = EPokerCardType.COW_DRAGON;
                    break;
                }
                cardValue = PokerUtil.isCowBomb(pl.getHandCard(), result);
                if (-1 != cardValue) {
                    cardType = EPokerCardType.COW_BOMB;
                    break;
                }
                cardValue = PokerUtil.isCowFiveSmall(pl.getHandCard(), result);
                if (-1 != cardValue) {
                    cardType = EPokerCardType.COW_FIVE_SMALL;
                    break;
                }
                cardValue = PokerUtil.isCowCucurbit(pl.getHandCard(), result);
                if (-1 != cardValue) {
                    cardType = EPokerCardType.COW_CUCURBIT;
                    break;
                }
                cardValue = PokerUtil.isCowGold(pl.getHandCard(), result);
                if (-1 != cardValue) {
                    cardType = EPokerCardType.COW_GOLD;
                    break;
                }
                cardValue = PokerUtil.isCowSameColor(pl.getHandCard(), result);
                if (-1 != cardValue) {
                    cardType = EPokerCardType.COW_SAME_COLOR;
                    break;
                }
                cardValue = PokerUtil.isCowSilver(pl.getHandCard(), result);
                if (-1 != cardValue) {
                    cardType = EPokerCardType.COW_SILVER;
                    break;
                }
                cardValue = PokerUtil.isCowStraight(pl.getHandCard(), result);
                if (-1 != cardValue) {
                    cardType = EPokerCardType.COW_STRAIGHT;
                    break;
                }
            } while (false);

            if (cardType == EPokerCardType.COW_NONE) {
                cardType = PokerUtil.findCow(pl.getHandCard(), result);
                byte value = pl.getHandCard().get(4);
                if (cardType == EPokerCardType.COW_NONE) {
                    cardValue = PokerUtil.generateCardValueByCow2WithColor(PokerUtil.getCardValueByCow2(value),
                            (byte) (PokerUtil.getCardColor(value) + 1)) * Math.pow(128, 0);
                } else {
                    cardValue = PokerUtil.generateCardValueByCow2WithColor(PokerUtil.getCardValueByCow2(value),
                            (byte) (PokerUtil.getCardColor(value) + 1)) * Math.pow(128, 1);
                }
            }
            
            js.put("playerUid", pl.getUid());
            js.put("curCardType", cardType.getValue());
            js.put("handCard", pl.getHandCard());
            System.out.println("playerUid:"+pl.getUid()+";curCardType:"+cardType.getValue()+";handCard:"+pl.getHandCard());
            array.add(js);
        }
        player.send(CommandId.CLI_NTF_POKER_COW_ROBOT_OK, array);
//        PCLIArenaReqGetCowRobot info = (PCLIArenaReqGetCOWRobot) request;
//        CowArenaRoom cowArenaRoom = (CowArenaRoom) RoomManager.I.getRoom(info.roomId);
//        //CowArenaRoom cowArenaRoom = (CowArenaRoom) ArenaManager.I.getArena(info.arenaUid);
//        if (cowArenaRoom == null) {
//            Logs.ARENA.warn("%s 竞技场不存在 info:%s", player, info);
//            player.send(CommandId.CLI_NTF_POKER_COW_ROBOT_FAIL, ErrorCode.ARENA_NOT_EXISTS);
//            return null;
//        }
//        PCLIArenaNtfGetCOWRobotInfo resqinfo = new PCLIArenaNtfGetCOWRobotInfo();
//        resqinfo.cards = cowArenaRoom.allMyCards(player.getUid());
//        resqinfo.fourCardType = cowArenaRoom.cardTypeBy4Cards(player.getUid());
//        resqinfo.isBiggerN9 = cowArenaRoom.getCowCardType(player.getUid()).ordinal() >= EPokerCardType.COW_9.ordinal();
//        resqinfo.fiveCardType = cowArenaRoom.getCowCardType(player.getUid()).ordinal();
//        for (Map.Entry<Long,Boolean> entry : cowArenaRoom.allCompareResult(player.getUid()).entrySet()) {
//            resqinfo.result.put(entry.getKey(),entry.getValue());
//        }
//        player.send(CommandId.CLI_NTF_POKER_COW_ROBOT_OK, resqinfo);
        return null;
    }
}
