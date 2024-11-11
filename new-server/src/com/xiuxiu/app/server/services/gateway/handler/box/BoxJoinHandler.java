package com.xiuxiu.app.server.services.gateway.handler.box;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.box.PCLIBoxReqJoinInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.BoxManager;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubCloseStatus;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.GameType;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.handle.AbstractBoxRoomHandle;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.message.Handler;

public class BoxJoinHandler implements Handler {

    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIBoxReqJoinInfo info = (PCLIBoxReqJoinInfo) request;
        if (-1 != player.getRoomId()) {
            Room room = RoomManager.I.getRoom(player.getRoomId());
            if (null == room) { 
                player.changeRoomId(-1, -1);
            } else {
                if ((room.getGameType() == GameType.GAME_TYPE_HUNDRED_LHD  || room.getGameType() == GameType.GAME_TYPE_HUNDRED_BACCARAT) 
                    && ((AbstractBoxRoomHandle) room.getRoomHandle()).getBoxUid() == info.boxUid) {

                } else {
                    Logs.CLUB.warn("%s 在房间中, 无法加入包厢", player);
                    player.send(CommandId.CLI_NTF_BOX_JOIN_FAIL, ErrorCode.PLAYER_ROOM_IN);
                    return null;
                }
            }
        }
        IClub fromClub = ClubManager.I.getClubByUid(info.clubUid);
        if (null == fromClub) {
            Logs.CLUB.warn("%s 无法加入包厢, 群:%d 不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_BOX_JOIN_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (!fromClub.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s 不在群:%d里, 无法加入包厢", player, info.clubUid);
            if(fromClub.getClubType()== EClubType.GOLD){
                player.send(CommandId.CLI_NTF_BOX_JOIN_FAIL, ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER);
            }else{
                player.send(CommandId.CLI_NTF_BOX_JOIN_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
            }
            return null;
        }
        IClub club = null;
        // 判断是否加入主圈
        if (fromClub.checkIsJoinInMainClub()) {
            long finalClubId = fromClub.getFinalClubId();
            if (finalClubId == 0) {
                Logs.CLUB.warn("%s 不在群:%d里, 无法加入包厢", player, info.clubUid);
                player.send(CommandId.CLI_NTF_BOX_JOIN_FAIL, ErrorCode.REQUEST_INVALID);
                return null;
            }
            club = ClubManager.I.getClubByUid(finalClubId);
        } else {
            club = fromClub;
        }
        Box box = club.getBox(info.boxUid);
        if (null == box) {
            Logs.CLUB.warn("%s 无法加入包厢, 群:%d 包厢:%d 不存在", player, info.clubUid, info.boxUid);
            if (info.roomIndex == -2) {
                player.send(CommandId.CLI_NTF_BOX_JOIN_FAIL, ErrorCode.ROOM_ALREADY_START);
            } else {
                player.send(CommandId.CLI_NTF_BOX_JOIN_FAIL, ErrorCode.GROUP_BOX_NOT_EXISTS);
            }
            return null;
        }
        if (fromClub.isForbidPlay(player.getUid())) {
            Logs.CLUB.warn("%s groupUid:%d 玩家被禁玩了", player, info.clubUid);
            player.send(CommandId.CLI_NTF_BOX_JOIN_FAIL, ErrorCode.ACCOUNT_GROUP_PLAYER_FORBIDO_LAY);
            return null;
        }
        
        // 是否已打烊
        if (club.matchCloseStatus(EClubCloseStatus.CLOSING) || club.matchCloseStatus(EClubCloseStatus.CLOSED)) {
            Logs.GROUP.warn("%s groupUid:%d 已打烊，不能玩", player, info.clubUid);
            ErrorCode ec = club.getClubType() == EClubType.CARD ? ErrorCode.CLUB_CLOSE_STATUS_LIMIT_ROOM_CARD : ErrorCode.CLUB_CLOSE_STATUS_LIMIT;
            player.send(CommandId.CLI_NTF_BOX_JOIN_FAIL, ec);
            return null;
        }

        if (!BoxManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s groupUid:%d 正在操作", player, info.clubUid);
            player.send(CommandId.CLI_NTF_BOX_JOIN_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            ErrorCode err = BoxManager.I.canJoin(player, fromClub, box, Boolean.FALSE);
            if (ErrorCode.OK == err) {
                club.playerEnterClub(player.getUid(), fromClub.getClubUid());
                ErrorCode ec = BoxManager.I.join(player, club, box, info.roomIndex);
                if (ec != null) {
                    if (ErrorCode.OK != ec) {
                        if (info.roomIndex == -2) {
                            player.send(CommandId.CLI_NTF_BOX_JOIN_FAST_FAIL, null);
                        } else {
                            player.send(CommandId.CLI_NTF_BOX_JOIN_FAIL, ec);
                        }
                    } else {
                        player.send(CommandId.CLI_NTF_BOX_JOIN_OK, null);
                    }
                }
            } else {
                player.send(CommandId.CLI_NTF_BOX_JOIN_FAIL, err);
            }
        } finally {
            BoxManager.I.unlock(player.getUid());
        }
        return null;
    }
}
