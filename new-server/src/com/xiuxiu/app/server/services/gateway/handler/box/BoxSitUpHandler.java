package com.xiuxiu.app.server.services.gateway.handler.box;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.box.PCLIBoxReqSitUp;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.BoxManager;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.handle.IBoxRoomHandle;
import com.xiuxiu.app.server.room.normal.IRoomPlayer;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.message.Handler;

/**
 * 亲友圈可少人模式-站起
 * 
 * @author Administrator
 *
 */
public class BoxSitUpHandler implements Handler {

    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIBoxReqSitUp info = (PCLIBoxReqSitUp) request;
        if (-1 == player.getRoomId()) {
            Logs.CLUB.warn("%s 不在房间中, 无法站起", player);
            player.send(CommandId.CLI_NTF_BOX_SIT_UP_FAIL, ErrorCode.PLAYER_ROOM_IN);
            return null;
        }

        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (null == club) {
            Logs.CLUB.warn("%s 无法站起, 群:%d 不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_BOX_SIT_UP_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (!club.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s 不在群:%d里, 无法站起", player, info.clubUid);
            if (club.getClubType() == EClubType.GOLD) {
                player.send(CommandId.CLI_NTF_BOX_SIT_UP_FAIL, ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER);
            } else {
                player.send(CommandId.CLI_NTF_BOX_SIT_UP_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
            }
            return null;
        }
        // 判断是否加入主圈
        if (club.checkIsJoinInMainClub()) {
            long finalClubId = club.getFinalClubId();
            if (finalClubId == 0) {
                Logs.CLUB.warn("%s 不在群:%d里, 无法站起", player, info.clubUid);
                player.send(CommandId.CLI_NTF_BOX_SIT_UP_FAIL, ErrorCode.REQUEST_INVALID);
                return null;
            }
            club = ClubManager.I.getClubByUid(finalClubId);
        }
        if (club.isForbidPlay(player.getUid())) {
            Logs.CLUB.warn("%s groupUid:%d 玩家被禁玩了", player, info.clubUid);
            player.send(CommandId.CLI_NTF_BOX_SIT_UP_FAIL, ErrorCode.ACCOUNT_GROUP_PLAYER_FORBIDO_LAY);
            return null;
        }

        Room room = RoomManager.I.getRoom(info.roomId);
        if (null == room) {
            return null;
        }
        IBoxRoomHandle boxRoomHandle = (IBoxRoomHandle) room.getRoomHandle();
        if (boxRoomHandle.hasPlayed(player.getUid())) {
            player.send(CommandId.CLI_NTF_BOX_SIT_UP_FAIL, ErrorCode.ROOM_SIT_UP_LIMIT);
            return null;
        }

        if (!BoxManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s groupUid:%d 正在操作", player, info.clubUid);
            player.send(CommandId.CLI_NTF_BOX_SIT_UP_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            if (room.getGameType() == GameType.GAME_TYPE_COW && room.getGameSubType() == 1 && room.getCurBureau() != 0){
                IRoomPlayer  roomPlayer= room.getRoomPlayer(player.getUid());
                if (roomPlayer != null && !roomPlayer.isGuest()){
                    player.send(CommandId.CLI_NTF_BOX_SIT_UP_FAIL, ErrorCode.ROOM_SIT_UP_LIMIT);
                    return null;
                }
            }
            ErrorCode err = BoxManager.I.sitUp(player, info.roomId, club);
            if (ErrorCode.OK == err) {
                
            } else if (ErrorCode.PLAYER_ARENA_ROOM_IN != err) {
                player.send(CommandId.CLI_NTF_BOX_SIT_UP_FAIL, err);
            }
        } finally {
            BoxManager.I.unlock(player.getUid());
        }
        return null;
    }

}
