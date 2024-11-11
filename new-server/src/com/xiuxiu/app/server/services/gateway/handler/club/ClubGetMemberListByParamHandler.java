package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfGetMemberListByParam;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqGetMemberListByParam;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;

import java.util.ArrayList;
import java.util.List;

public class ClubGetMemberListByParamHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqGetMemberListByParam info = (PCLIClubReqGetMemberListByParam) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null) {
            Logs.CLUB.warn("%s club:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_GET_CLUBMEMBER_BY_PARAM_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        Player tempPlayer = PlayerManager.I.getPlayer(info.param);
        if (tempPlayer == null) {
            Logs.CLUB.warn("%s player:%d 玩家不存在", player, info.param);
            player.send(CommandId.CLI_NTF_CLUB_GET_CLUBMEMBER_BY_PARAM_FAIL, ErrorCode.PLAYER_NOT_EXISTS);
            return null;
        }
        PCLIClubNtfGetMemberListByParam respInfo = new PCLIClubNtfGetMemberListByParam();
        respInfo.clubUid = info.clubUid;
        respInfo.param = info.param;
        //本圈
        if (info.type == 0) {
            if (!club.hasMember(info.param)) {
                Logs.CLUB.warn("%s player:%d 玩家不在圈中", player, info.param);
                player.send(CommandId.CLI_NTF_CLUB_GET_CLUBMEMBER_BY_PARAM_FAIL, ErrorCode.CLUB_NOT_PLAYER);
                return null;
            }
            ClubMember clubMember = club.getMember(info.param);
            PCLIClubNtfGetMemberListByParam.memberList temp = new PCLIClubNtfGetMemberListByParam.memberList();
            temp.playerUid = info.param;
            temp.icon = tempPlayer.getIcon();
            temp.name = tempPlayer.getName();
            temp.joinTime = clubMember.getJoinTime();
            temp.jobType = clubMember.getJobType();
            temp.privilege = clubMember.getPrivilege();
            temp.showNick = clubMember.getShowNick();
            //查看是否在线
            if (tempPlayer.isOnline()) {
                temp.offlineTime = 0;
            } else {
                temp.offlineTime = tempPlayer.getLastLogoutTime();
            }
            temp.uplinePlayerUid = clubMember.getUplinePlayerUid();
            temp.state = clubMember.getState();
            temp.score = club.getMemberExt(clubMember.getPlayerUid(),true).getGold();
            temp.divide = clubMember.getDivide();
            temp.divideLine = clubMember.getDivideLine();
            temp.isUpGoldTreasurer = club.checkIsUpTreasurer(info.param);
            temp.isDownGoldTreasurer = club.checkIsDownTreasurer(info.param);
            respInfo.lists.add(temp);
        } else {
            //没合过圈
            if (!club.checkIsJoinInMainClub()) {
                Logs.CLUB.warn("%s club:%d没有合并过", player, info.clubUid);
                player.send(CommandId.CLI_NTF_CLUB_GET_CLUBMEMBER_BY_PARAM_FAIL, ErrorCode.CLUB_NOT_HAVE_MERGE);
                return null;
            }
            //总圈
            long rootCLubUid = club.getFinalClubId();
            IClub rootClub = ClubManager.I.getClubByUid(rootCLubUid);
            List<Long> allChildClubs = new ArrayList<>();
            rootClub.fillDepthChildClubUidList(allChildClubs);
            allChildClubs.add(0,rootCLubUid);
            for (Long m_clubUid : allChildClubs) {
                IClub m_club = ClubManager.I.getClubByUid(m_clubUid);
                if (m_club == null) {
                    continue;
                }
                if (!m_club.hasMember(info.param)) {
                    continue;
                }
                ClubMember clubMember = m_club.getMember(info.param);
                if (clubMember == null) {
                    continue;
                }
                PCLIClubNtfGetMemberListByParam.memberList temp = new PCLIClubNtfGetMemberListByParam.memberList();
                temp.playerUid = info.param;
                temp.icon = tempPlayer.getIcon();
                temp.name = tempPlayer.getName();
                temp.joinTime = clubMember.getJoinTime();
                temp.jobType = clubMember.getJobType();
                temp.privilege = clubMember.getPrivilege();
                temp.showNick = clubMember.getShowNick();
                //查看是否在线
                if (tempPlayer.isOnline()) {
                    temp.offlineTime = 0;
                } else {
                    temp.offlineTime = tempPlayer.getLastLogoutTime();
                }
                temp.uplinePlayerUid = clubMember.getUplinePlayerUid();
                temp.state = clubMember.getState();
                temp.score = m_club.getMemberExt(clubMember.getPlayerUid(),true).getGold();
                temp.divide = clubMember.getDivide();
                temp.divideLine = clubMember.getDivideLine();
                temp.isUpGoldTreasurer = m_club.checkIsUpTreasurer(info.param);
                temp.isDownGoldTreasurer = m_club.checkIsDownTreasurer(info.param);
                respInfo.lists.add(temp);
                break;
            }
        }

        player.send(CommandId.CLI_NTF_CLUB_GET_CLUBMEMBER_BY_PARAM_OK, respInfo);
        return null;
    }
}
