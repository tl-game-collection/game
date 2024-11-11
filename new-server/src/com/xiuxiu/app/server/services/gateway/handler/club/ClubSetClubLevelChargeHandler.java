package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfSetClubLevelCharge;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqSetClubLevelCharge;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

/**
 * 修改下级圈的管理费
 */
public class ClubSetClubLevelChargeHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqSetClubLevelCharge info = (PCLIClubReqSetClubLevelCharge) request;

        //check something
        if (info.serviceCharge < 0) {
            Logs.CLUB.warn("%s clubUid:%d 无效请求", player, info.serviceCharge);
            player.send(CommandId.CLI_NTF_CLUB_SET_MAINCHARGE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }

        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        IClub setClub = ClubManager.I.getClubByUid(info.setClubUid);
        if (null == club || setClub == null) {
            Logs.CLUB.warn("%s clubUid:%d 俱乐部不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_MAINCHARGE_FAIL, ErrorCode.CLUB_NOT_EXISTS);
            return null;
        }

        if (!club.checkIsJoinInMainClub()) {
            Logs.CLUB.warn("%s clubUid:%d 没合过圈", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_MAINCHARGE_FAIL, ErrorCode.CLUB_NOT_HAVE_MERGE);
            return null;
        }

        if (club.getOwnerId() != player.getUid()) {
            Logs.CLUB.warn("%s clubUid:%d 不是圈主", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_MAINCHARGE_FAIL, ErrorCode.CLUB_NOT_CHIEF_NO_PRIVILEGE);
            return null;
        }

        //deal
        PCLIClubNtfSetClubLevelCharge resp = new PCLIClubNtfSetClubLevelCharge();
        resp.setClubUid = info.setClubUid;
        resp.serviceCharge = info.serviceCharge;
        //如果是主圈
        if (club.checkIsMainClub()) {
            if (setClub.getFinalClubId() != club.getClubUid()) {
                Logs.CLUB.warn("%s clubUid:%d 不在主圈中", player, info.setClubUid);
                player.send(CommandId.CLI_NTF_CLUB_SET_MAINCHARGE_FAIL, ErrorCode.CLUB_NOT_IS_IN_MAIN);
                return null;
            }
        }
        //不是主圈就只能是一级圈
        else if (club.checkIsLevelOneClub()) {
            if (setClub.getClubInfo().getParentUid() != club.getClubUid()) {
                Logs.CLUB.warn("%s clubUid:%d 不在一级圈中", player, info.setClubUid);
                player.send(CommandId.CLI_NTF_CLUB_SET_MAINCHARGE_FAIL, ErrorCode.CLUB_NOT_IS_IN_ONELEVEL);
                return null;
            }
        } else {
            Logs.CLUB.warn("%s clubUid:%d 亲友圈不是主圈也不是一级圈", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_MAINCHARGE_FAIL, ErrorCode.CLUB_NOT_IS_MAINCLUB_OR_ONELEVEL);
            return null;
        }

        club.getClubInfo().getServiceChargeMap().put(info.setClubUid,info.serviceCharge);
        club.getClubInfo().setDirty(true);
        player.send(CommandId.CLI_NTF_CLUB_SET_MAINCHARGE_OK, resp);
        return null;
    }
}
