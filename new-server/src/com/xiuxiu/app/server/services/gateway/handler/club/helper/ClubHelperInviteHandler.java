package com.xiuxiu.app.server.services.gateway.handler.club.helper;

import java.util.ArrayList;
import java.util.List;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.helper.PCLIClubNtfHelperInviteNotice;
import com.xiuxiu.app.protocol.client.club.helper.PCLIClubReqHelperInvite;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.BoxManager;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.helper.ClubHelperManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.ERoomState;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.handle.IBoxRoomHandle;
import com.xiuxiu.app.server.room.handle.IRoomHandle;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.core.net.message.Handler;

/**
 * 邀请在线玩家一起游戏
 * 
 * @author Administrator
 *
 */
public class ClubHelperInviteHandler implements Handler {

    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqHelperInvite info = (PCLIClubReqHelperInvite) request;
        IClub tempClub = ClubManager.I.getClubByUid(info.clubUid);
        if (null == tempClub) {
            Logs.GROUP.warn("%s groupUid:%d 群不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_PLAYERS_FAIL, ErrorCode.GROUP_NOT_EXISTS);
            return null;
        }
        IClub fromClub = null, mainClub = null;
        if (tempClub.checkIsMainClub()) {
            mainClub = tempClub;            
            fromClub = ClubManager.I.getClubByUid(tempClub.getEnterFromClubUid(player.getUid()));
        } else {            
            mainClub = ClubManager.I.getClubByUid(tempClub.getFinalClubId());
            if (null == mainClub) {
                mainClub = tempClub;
                fromClub = tempClub;
            } else {
                fromClub = ClubManager.I.getClubByUid(mainClub.getEnterFromClubUid(player.getUid()));
            }
        }
        if (null == fromClub || !fromClub.hasMember(player.getUid())) {
            Logs.GROUP.warn("%s groupUid:%d 群不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_FAIL, ErrorCode.GROUP_NOT_EXISTS);
            return null;
        }
        if (!fromClub.hasMember(player.getUid())) {
            Logs.GROUP.warn("%s groupUid:%d 不在群里", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_FAIL, ErrorCode.GROUP_NOT_IN);
            return null;
        }
        if (player.getRoomId() == -1) {
            player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_FAIL, ErrorCode.REQUEST_INVALID);
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

        if (fromClub.checkIsJoinInMainClub()) {
            mainClub = ClubManager.I.getClubByUid(fromClub.getFinalClubId());
            if (box.getOwnerUid() != mainClub.getClubUid()) {
                return null;
            }
        } else {
            if (box.getOwnerUid() != mainClub.getClubUid()) {
                return null;
            }
        }
        
        if (null == room || player.getRoomId() != room.getRoomId()) {
            player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_FAIL, ErrorCode.REQUEST_INVALID);
            return null;
        }

        if (room.getRoomState() != ERoomState.NEW) {
            player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_FAIL, ErrorCode.CLUB_HELPER_3);
            return null;
        }
        
        if (info.inviteUid == -1) {
            List<Long> memberUids = fromClub.getInviteMemberUids(player, room);
            if (memberUids != null && memberUids.size() > 0) {
            //  通知被邀请人
                PCLIClubNtfHelperInviteNotice message = new PCLIClubNtfHelperInviteNotice();
                message.clubUid = fromClub.getClubUid();
                message.clubName = fromClub.getName();
                message.type = fromClub.getClubType().getType();
                message.gameSubType = room.getGameSubType();
                message.gameType = room.getGameType();
                message.rule.putAll(room.getRule());
                message.roomId = room.getRoomId();
                message.name = player.getName();
                message.icon = player.getIcon();
                
                for (long memberUid : memberUids) {
                    if (memberUid >= 300000 && memberUid <= 400000) {
                        continue;
                    }
                    if (!PlayerManager.I.isOnline(memberUid)) {
                        continue;
                    }
                    Player invitePlayer = PlayerManager.I.getOnlinePlayer(memberUid);
                    if (null == invitePlayer) {
                        continue;
                    }
                    // 是否在游戏中
                    if (invitePlayer.getRoomId() != -1) {
                        continue;
                    }
                    if (!ClubHelperManager.I.isAllowInvite(memberUid)) {
                        continue;
                    }

                    // 被邀请人是否接受(0拒绝1接受)
                    message.status = ClubHelperManager.I.isAllowInvite(memberUid) ? 1 : 0;
                    invitePlayer.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_NOTICE, message);
                }
            }
        } else {
            // 判断被邀请人是否在线
            if (!PlayerManager.I.isOnline(info.inviteUid)) {
                player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_FAIL, ErrorCode.CLUB_HELPER_1);
                return null;
            }
            Player invitePlayer = PlayerManager.I.getOnlinePlayer(info.inviteUid);
            if (null == invitePlayer) {
                player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_FAIL, ErrorCode.CLUB_HELPER_1);
                return null;
            }
            // 是否在游戏中
            if (invitePlayer.getRoomId() != -1) {
                player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_FAIL, ErrorCode.CLUB_HELPER_2);
                return null;
            }
            
            if (!ClubHelperManager.I.isAllowInvite(info.inviteUid)) {
                player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_FAIL, ErrorCode.CLUB_HELPER_5);
                return null;
            }
    
            if (!ClubManager.I.lock(player.getUid())) {
                Logs.GROUP.warn("%s groupUid:%d 正在操作", player, info.clubUid);
                player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_FAIL, ErrorCode.REPEAT_OPERATE);
                return null;
            }
            try {
                //  通知被邀请人
                PCLIClubNtfHelperInviteNotice message = new PCLIClubNtfHelperInviteNotice();
                message.clubUid = fromClub.getClubUid();
                message.clubName = fromClub.getName();
                message.type = fromClub.getClubType().getType();
                message.gameSubType = room.getGameSubType();
                message.gameType = room.getGameType();
                message.rule.putAll(room.getRule());
                message.roomId = room.getRoomId();
                message.name = player.getName();
                message.icon = player.getIcon();
                // 被邀请人是否接受(0拒绝1接受)
                message.status = ClubHelperManager.I.isAllowInvite(info.inviteUid) ? 1 : 0;
                
                invitePlayer.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_NOTICE, message);
                player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_OK, null);
            } finally {
                ClubManager.I.unlock(player.getUid());
            }
        }
        return null;
    }
}
