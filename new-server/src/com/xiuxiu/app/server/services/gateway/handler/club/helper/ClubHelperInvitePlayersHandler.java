package com.xiuxiu.app.server.services.gateway.handler.club.helper;

import java.util.ArrayList;
import java.util.List;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.helper.PCLIClubNtfHelperInvitePlayers;
import com.xiuxiu.app.protocol.client.club.helper.PCLIClubReqHelperInvitePlayers;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.Box;
import com.xiuxiu.app.server.box.BoxManager;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.handle.IBoxRoomHandle;
import com.xiuxiu.app.server.room.handle.IRoomHandle;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.core.net.message.Handler;

/**
 * 获取邀请信息列表
 * 
 * @author Administrator
 *
 */
public class ClubHelperInvitePlayersHandler implements Handler {
    
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqHelperInvitePlayers info = (PCLIClubReqHelperInvitePlayers) request;
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
            Logs.GROUP.warn("%s groupUid:%d 不在群里", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_PLAYERS_FAIL, ErrorCode.GROUP_NOT_IN);
            return null;
        }
        IRoom room = RoomManager.I.getRoom(player.getRoomId());
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
        if (!ClubManager.I.lock(player.getUid())) {
            Logs.GROUP.warn("%s groupUid:%d 正在操作", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_PLAYERS_FAIL, ErrorCode.REPEAT_OPERATE);
            return null;
        }
        try {
            List<Long> memberUids = fromClub.getInviteMemberUids(player, room);
            PCLIClubNtfHelperInvitePlayers message = new PCLIClubNtfHelperInvitePlayers();
            message.clubUid = info.clubUid;
            message.page = info.page;
            int count = null == memberUids ? 0 : memberUids.size();
            if (count > 0) {
                int fromIndex = info.page * info.size;
                int toIndex = (info.page + 1) * info.size;
                if (toIndex > count) {
                    toIndex = count;
                }
                int totalPage = count / info.size;
                if (totalPage % info.size != 0) {
                    totalPage++;
                }

                totalPage = totalPage == 0 ? 1 : totalPage;

                message.next = info.page < totalPage - 1;
                List<PCLIClubNtfHelperInvitePlayers.HelperInvitePlayersInfo> tempList = new ArrayList<PCLIClubNtfHelperInvitePlayers.HelperInvitePlayersInfo>();
                List<Long> currentPageMemberUids = memberUids.subList(fromIndex, toIndex);
                for (Long memberUid : currentPageMemberUids) {
                    Player tempPlayer = PlayerManager.I.getOnlinePlayer(memberUid);
                    if (null == tempPlayer || tempPlayer.getRoomId() > 0) {
                        continue;
                    }
                    PCLIClubNtfHelperInvitePlayers.HelperInvitePlayersInfo tempInfo = new PCLIClubNtfHelperInvitePlayers.HelperInvitePlayersInfo();
                    tempInfo.name = tempPlayer.getName();
                    tempInfo.icon = tempPlayer.getIcon();
                    tempInfo.id = memberUid;
                    tempList.add(tempInfo);
                }
                message.list = tempList;
            }
            player.send(CommandId.CLI_NTF_CLUB_HELPER_INVITE_PLAYERS_OK, message);
            return null;
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
    }
}
