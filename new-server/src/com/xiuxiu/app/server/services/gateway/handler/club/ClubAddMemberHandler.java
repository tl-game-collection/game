package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqAddMember;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.EPlayerPrivilege;
import com.xiuxiu.app.server.player.EPlayerPrivilegeLevel;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;

public class ClubAddMemberHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqAddMember info = (PCLIClubReqAddMember) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null) {
            Logs.CLUB.warn("%s player:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_ADDMEMBER_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (!club.hasMember(player.getUid())){
            Logs.CLUB.warn("%s player:%d邀请人不在俱乐部中", player, info.clubUid);
            ErrorCode code = club.getClubType() == EClubType.GOLD ? ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER : ErrorCode.GROUP_NOT_IN;
            player.send(CommandId.CLI_NTF_CLUB_ADDMEMBER_FAIL, code);
            return null;
        }
        Player infoPlayer = PlayerManager.I.getPlayer(info.playerUid);
        if (null == infoPlayer) {
            Logs.CLUB.warn("%d clubUid:%d 被邀请玩家不存在", info.clubUid, info.playerUid);
            player.send(CommandId.CLI_NTF_CLUB_ADDMEMBER_FAIL, ErrorCode.PLAYER_NOT_EXISTS);
            return null;
        }
        if (club.hasMember(info.playerUid)) {
            Logs.CLUB.warn("%s infoPlayer:%d被邀请玩家已经在俱乐部中", infoPlayer, info.clubUid);
            ErrorCode code = club.getClubType() == EClubType.GOLD ? ErrorCode.CLUB_GOLD_ALREADY_IN : ErrorCode.CLUB_CARD_ALREADY_IN;
            player.send(CommandId.CLI_NTF_CLUB_ADDMEMBER_FAIL, code);
            return null;
        }
        Player ownerPlayer = PlayerManager.I.getPlayer(club.getOwnerId());
        if (club.getMemberCnt() > EPlayerPrivilegeLevel.getValue(ownerPlayer.getPrivilege(), EPlayerPrivilege.GROUP_MEMBER_NUM)) {
            Logs.CLUB.warn("%s infoPlayer:%d 该俱乐部人员已满 ", infoPlayer, info.clubUid);
            ErrorCode code = club.getClubType() == EClubType.GOLD ? ErrorCode.ROOM_GOLD_MEMBER_FULL : ErrorCode.CLUB_CARD_MEMBER_FULL;
            player.send(CommandId.CLI_NTF_CLUB_ADDMEMBER_FAIL, code);
            return null;
        }
//        long invitorPlayerUid;
//        long recommendUid = infoPlayer.getRecommendInfo().getRecommendPlayerUid();
//        if (recommendUid > 0) {
//            if (!club.hasMember(recommendUid)) {
//                if (infoPlayer.isClub() > 0) {
//                    Logs.CLUB.warn("%s infoPlayer:%d 已经有推荐人 、 玩家有群", infoPlayer, info.clubUid);
//                    ErrorCode code = club.getClubType() == EClubType.GOLD ? ErrorCode.CLUB_GOLD_REOMMEND : ErrorCode.CLUB_CARD_REOMMEND;
//                    player.send(CommandId.CLI_NTF_CLUB_ADDMEMBER_FAIL, code);
//                    return null;
//                }
//                invitorPlayerUid = player.getUid();
//            } else {
//                invitorPlayerUid = recommendUid;
//            }
//        } else {
//            invitorPlayerUid = player.getUid();
//        }
        club.addMember(player.getUid(), infoPlayer, EClubJobType.NORMAL);
        player.send(CommandId.CLI_NTF_CLUB_ADDMEMBER_OK, null);
        return null;
    }
}
