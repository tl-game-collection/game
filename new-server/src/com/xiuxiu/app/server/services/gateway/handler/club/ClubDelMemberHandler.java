package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqDelMember;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;

public class ClubDelMemberHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqDelMember info = (PCLIClubReqDelMember) request;
        if (player.getUid() == info.playerUid) {
            Logs.CLUB.warn("%s club:%d玩家不能踢自己", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_DELMEMBER_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null) {
            Logs.CLUB.warn("%s club:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_DELMEMBER_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        //圈主不能被踢出去
        if (info.playerUid == club.getOwnerId()) {
            Logs.CLUB.warn("%s club:%d圈主不能被踢出去", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_DELMEMBER_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        Player delPlayer = PlayerManager.I.getPlayer(info.playerUid);
        if (delPlayer == null) {
            Logs.CLUB.warn("%s club:%d玩家不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_DELMEMBER_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        if (!club.hasMember(info.playerUid)) {
            Logs.CLUB.warn("%s club:%d玩家不在圈中", player, info.clubUid);
            ErrorCode ec = club.getClubType() == EClubType.CARD ? ErrorCode.CLUB_NOT_HAVE_PLAYER : ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER;
            player.send(CommandId.CLI_NTF_CLUB_DELMEMBER_FAIL, ec);
            return null;
        }
        //圈主和副圈主有权限
        ClubMember clubMember = club.getMember(player.getUid());
        if (club.getOwnerId() != player.getUid() && !clubMember.checkJobType(EClubJobType.DEPUTY)) {
            Logs.CLUB.warn("%s club:%d没有权限", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_DELMEMBER_FAIL, ErrorCode.CLUB_NO_PRIVILEGE);
            return null;
        }
        club.delMember(info.playerUid,player.getUid());
        Logs.CLUB.error("操作删除玩家：%d club:%d 被删除玩家：%d", player.getUid(), info.clubUid, info.playerUid);
        player.send(CommandId.CLI_NTF_CLUB_DELMEMBER_OK, null);
        return null;
    }
}
