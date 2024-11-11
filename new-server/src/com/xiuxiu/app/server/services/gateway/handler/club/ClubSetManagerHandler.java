package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfSetManager;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqSetManager;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置club管理员 (合圈后)
 */
public class ClubSetManagerHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqSetManager info = (PCLIClubReqSetManager) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null) {
            Logs.CLUB.warn("%s club:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_MANAGER_INFO_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        //没有合过圈
        if (!club.checkIsJoinInMainClub()) {
            Logs.CLUB.warn("%s club:%d没有合过圈", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_MANAGER_INFO_FAIL, ErrorCode.GM_INVALID_OPERATE);
            return null;
        }
        //不是总圈
        if (!club.checkIsMainClub()) {
            Logs.CLUB.warn("%s club:%d不是总圈", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_MANAGER_INFO_FAIL, ErrorCode.GM_INVALID_OPERATE);
            return null;
        }
        //操作人不是总圈圈主
        if (club.getOwnerId() != player.getUid()) {
            Logs.CLUB.warn("%s 不是总圈圈主", player);
            player.send(CommandId.CLI_NTF_CLUB_SET_MANAGER_INFO_FAIL, ErrorCode.CLUB_NOT_CHIEF_NO_PRIVILEGE);
            return null;
        }
        //要操作的亲友圈是否在总圈中
        List<Long> allClubUidList = new ArrayList<>();
        club.fillDepthChildClubUidList(allClubUidList);
        allClubUidList.add(0,club.getClubUid());
        for (int i = 0; i < info.managerClubList.size(); i++) {
            if (!allClubUidList.contains(info.managerClubList.get(i))) {
                Logs.CLUB.warn("%s club:%d总圈不包含此亲友圈", player, info.managerClubList.get(i));
                player.send(CommandId.CLI_NTF_CLUB_SET_MANAGER_INFO_FAIL, ErrorCode.REQUEST_INVALID_DATA);
                return null;
            }
        }
        //被操作人不能是总圈圈主
        if (club.getOwnerId() == info.playerUid) {
            Logs.CLUB.warn("%d 不能是总圈圈主", info.playerUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_MANAGER_INFO_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        //被操作人是否在club中
        boolean isIn = false;
        for (Long tempClubUid : allClubUidList) {
            IClub tempClub = ClubManager.I.getClubByUid(tempClubUid);
            if (tempClub == null) {
                continue;
            }
            if (tempClub.hasMember(info.playerUid)) {
                isIn = true;
                break;
            }
        }
        if (!isIn) {
            Logs.CLUB.warn("%s 没有这个玩家player:%d", player, info.clubUid, info.playerUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_MANAGER_INFO_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        player.send(CommandId.CLI_NTF_CLUB_SET_MANAGER_INFO_OK, null);

        PCLIClubNtfSetManager respInfo = new PCLIClubNtfSetManager();
        respInfo.clubUid = info.clubUid;
        respInfo.playerUid = info.playerUid;
        respInfo.managerClubList = info.managerClubList;
        club.broadcastAllLowClub(CommandId.CLI_NTF_CLUB_SET_MANAGER_INFO, respInfo);

        //设置管理员
        club.setManager(player.getUid(),info.playerUid,info.managerClubList);
        return null;
    }
}
