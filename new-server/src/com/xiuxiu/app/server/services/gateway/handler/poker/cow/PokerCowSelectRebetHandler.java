package com.xiuxiu.app.server.services.gateway.handler.poker.cow;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.poker.cow.PCIPokerNtfSelectRebetInfo;
import com.xiuxiu.app.protocol.client.poker.cow.PCIPokerReqSelectRebetInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.poker.IPokerRoom;
import com.xiuxiu.app.server.room.normal.poker.cow.CowRoom;
import com.xiuxiu.core.net.message.Handler;

/**
 * @auther: yuyunfei
 * @date: 2020/1/8 14:52
 * @comment:
 */
public class PokerCowSelectRebetHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
//        Player player = (Player) owner;
//        PCIPokerReqSelectRebetInfo info = (PCIPokerReqSelectRebetInfo) request;
//        Group group = GroupManager.I.getGroupByUid(info.groupUid);
//        if (group == null) {
//            Logs.ROOM.warn("%s 找不到群", info.groupUid);
//            player.send(CommandId.CLI_NTF_SELECT_REBET_FAIL, ErrorCode.GROUP_NOT_EXISTS);
//            return null;
//        }
//        boolean isAutoMode = group.hasPrivilege(player.getUid(), GroupPrivilege.PLAY_AUTO_MODE);
//        if (!isAutoMode) {
//            Logs.ROOM.warn("%s 没有托管权限", player);
//            player.send(CommandId.CLI_NTF_SELECT_REBET_FAIL, ErrorCode.GROUP_NOT_PRIVILEGE_AUTO_MODE);
//            return null;
//        }
//        IArena arena = ArenaManager.I.getArena(info.arenaUid);
//        if (arena == null || arena.getInfo().getGroupUid() != group.getUid()) {
//            player.send(CommandId.CLI_NTF_SELECT_REBET_FAIL, ErrorCode.ARENA_NOT_EXISTS);
//            return null;
//        }
//        IPokerRoom room = (IPokerRoom) RoomManager.I.getRoom(player.getRoomId());
//        if (null == room) {
//            Logs.ROOM.warn("%s 不在房间里", player);
//            player.send(CommandId.CLI_NTF_SELECT_REBET_FAIL, ErrorCode.ROOM_NOT_EXISTS);
//            return null;
//        }
//        PCIPokerNtfSelectRebetInfo resp = new PCIPokerNtfSelectRebetInfo();
//        if (room.getGameType() == GameType.GAME_TYPE_COW) {
//            boolean isMaxRebet = ((CowRoom) room).selectRebet(player.getUid());
//            resp.isMaxRebet = isMaxRebet;
//        } else if (room.getGameType() == GameType.GAME_TYPE_FRIED_GOLDEN_FLOWER) {
//            ((FGFRoom) room).selectRebet(player.getUid(), resp);
//        }
//        player.send(CommandId.CLI_NTF_SELECT_REBET_OK, resp);

        return null;
    }
}
