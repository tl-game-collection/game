package com.xiuxiu.app.server.services.gateway.handler.club.helper;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.helper.PCLIClubReqHelperInviteAnswer;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.BoxManager;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubCloseStatus;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.ERoomState;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.handle.IBoxRoomHandle;
import com.xiuxiu.app.server.room.handle.IRoomHandle;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.core.net.message.Handler;

/**
 * 响应游戏邀请
 * 
 * @author Administrator
 *
 */
public class ClubHelperInviteAnswerHandler implements Handler {

    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqHelperInviteAnswer info = (PCLIClubReqHelperInviteAnswer) request;
        if (info.status == 0) {
            return null;
        }
        IClub fromClub = ClubManager.I.getClubByUid(info.clubUid);
        if (null == fromClub) {
            Logs.GROUP.warn("%s groupUid:%d 群不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_ANSWER_FAIL, ErrorCode.GROUP_NOT_EXISTS);
            return null;
        }
        if (!fromClub.hasMember(player.getUid())) {
            Logs.GROUP.warn("%s groupUid:%d 不在群里", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_ANSWER_FAIL, ErrorCode.GROUP_NOT_IN);
            return null;
        }

        // 是否在游戏中
        if (player.getRoomId() != -1) {
            player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_ANSWER_FAIL, ErrorCode.CLUB_HELPER_4);
            return null;
        }
        IRoom room = RoomManager.I.getRoom(info.roomId);
        if (null == room) {
            return null;
        }
        IRoomHandle roomHandle = room.getRoomHandle();
        if (!(roomHandle instanceof IBoxRoomHandle)) {
            return null;
        }
        IBoxRoomHandle boxRoomHandle = (IBoxRoomHandle)roomHandle;
        Box box = BoxManager.I.getBox(boxRoomHandle.getBoxUid());
        if (null == box) {
            return null;
        }

        IClub mainClub = null;
        if (fromClub.checkIsJoinInMainClub()) {
            mainClub = ClubManager.I.getClubByUid(fromClub.getFinalClubId());
            if (box.getOwnerUid() != mainClub.getClubUid()) {
                return null;
            }
        } else {
            if (box.getOwnerUid() != fromClub.getClubUid()) {
                return null;
            }
            mainClub = fromClub;
        }
        
        IClub club = null;
        // 判断是否加入主圈
        if (fromClub.checkIsJoinInMainClub()) {
            long finalClubId = fromClub.getFinalClubId();
            if (finalClubId == 0) {
                Logs.GROUP.warn("%s 不在群:%d里, 无法加入包厢", player, info.clubUid);
                player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_ANSWER_FAIL, ErrorCode.REQUEST_INVALID);
                return null;
            }
            club = ClubManager.I.getClubByUid(finalClubId);
        } else {
            club = fromClub;
        }
        if (fromClub.isForbidPlay(player.getUid())) {
            Logs.GROUP.warn("%s groupUid:%d 玩家被禁玩了", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_ANSWER_FAIL, ErrorCode.ACCOUNT_GROUP_PLAYER_FORBIDO_LAY);
            return null;
        }

        // 是否已打烊
        if (club.matchCloseStatus(EClubCloseStatus.CLOSING) || club.matchCloseStatus(EClubCloseStatus.CLOSED)) {
            Logs.GROUP.warn("%s groupUid:%d 已打烊，不能玩", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_ANSWER_FAIL, ErrorCode.CLUB_CLOSE_STATUS_LIMIT);
            return null;
        }

        if (!BoxManager.I.lock(player.getUid())) {
            Logs.GROUP.warn("%s groupUid:%d 正在操作", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_ANSWER_FAIL, ErrorCode.REPEAT_OPERATE);
            return null;
        }
        try {
            if (room.getRoomState() != ERoomState.NEW) {
                player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_ANSWER_FAIL, ErrorCode.CLUB_HELPER_4);
                return null;
            }
            ErrorCode err = BoxManager.I.canJoin(player, fromClub, box, Boolean.FALSE);
            if (ErrorCode.OK == err) {
                club.playerEnterClub(player.getUid(), fromClub.getClubUid());
                ErrorCode ec = BoxManager.I.join(player, club, box, boxRoomHandle.getIndex());
                if (ErrorCode.OK != ec) {
                    player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_ANSWER_FAIL, ec);
                } else {
                    player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_ANSWER_OK, null);
                }
            } else {
                player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_ANSWER_FAIL, err);
            }
            return null;
        } finally {
            BoxManager.I.unlock(player.getUid());
        }
    }
}
