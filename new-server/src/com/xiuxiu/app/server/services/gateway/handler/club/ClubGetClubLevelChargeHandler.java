package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfGetClubLevelCharge;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqGetClubLevelCharge;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取下级圈的管理费
 */
public class ClubGetClubLevelChargeHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqGetClubLevelCharge info = (PCLIClubReqGetClubLevelCharge) request;

        //check something
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (null == club) {
            Logs.CLUB.warn("%s clubUid:%d 俱乐部不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_GET_MAINCHARGE_FAIL, ErrorCode.CLUB_NOT_EXISTS);
            return null;
        }

        if (!club.checkIsJoinInMainClub()) {
            Logs.CLUB.warn("%s clubUid:%d 没合过圈", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_GET_MAINCHARGE_FAIL, ErrorCode.CLUB_NOT_HAVE_MERGE);
            return null;
        }

        if (club.getOwnerId() != player.getUid()) {
            Logs.CLUB.warn("%s clubUid:%d 不是圈主", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_GET_MAINCHARGE_FAIL, ErrorCode.CLUB_NOT_CHIEF_NO_PRIVILEGE);
            return null;
        }

        PCLIClubNtfGetClubLevelCharge resp = new PCLIClubNtfGetClubLevelCharge();
        //如果是主圈
        if (club.checkIsMainClub()) {
            List<Long> allClubUids = new ArrayList<>();
            club.fillDepthChildClubUidList(allClubUids);
            allClubUids.add(0,info.clubUid);
            for (long tempClubUid : allClubUids) {
                this.fillClubLevelChargeInfo(resp,tempClubUid,club);
            }
        }
        //不是主圈就只能是一级圈
        else if (club.checkIsLevelOneClub()) {
            for (long tempClubUid : club.getClubInfo().getChildUid()) {
                this.fillClubLevelChargeInfo(resp,tempClubUid,club);
            }
        } else {
            Logs.CLUB.warn("%s clubUid:%d 亲友圈不是主圈也不是一级圈", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_GET_MAINCHARGE_FAIL, ErrorCode.CLUB_NOT_IS_MAINCLUB_OR_ONELEVEL);
            return null;
        }

        player.send(CommandId.CLI_NTF_CLUB_GET_MAINCHARGE_OK, resp);
        return null;
    }

    private void fillClubLevelChargeInfo(PCLIClubNtfGetClubLevelCharge resp,long clubUid,IClub club){
        IClub tempClub = ClubManager.I.getClubByUid(clubUid);
        if (tempClub == null) {
            return;
        }
        PCLIClubNtfGetClubLevelCharge.ClubLevelChargeInfo  clubLevelChargeInfo = new PCLIClubNtfGetClubLevelCharge.ClubLevelChargeInfo();
        clubLevelChargeInfo.clubUid = clubUid;
        clubLevelChargeInfo.clubName = tempClub.getName();
        clubLevelChargeInfo.serviceCharge = club.getClubInfo().getServiceChargeMap().getOrDefault(clubUid,0);
        resp.list.add(clubLevelChargeInfo);
    }
}
