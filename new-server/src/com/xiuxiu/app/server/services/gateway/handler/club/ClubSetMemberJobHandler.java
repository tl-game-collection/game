package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqSetMemberJob;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;

public class ClubSetMemberJobHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqSetMemberJob info = (PCLIClubReqSetMemberJob) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null) {
            Logs.CLUB.warn("%s club:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_MEMBERJOB_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (player.getUid() != club.getOwnerId()) {
            Logs.CLUB.warn("%s club:%d没有权限", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_MEMBERJOB_FAIL, ErrorCode.CLUB_NOT_CHIEF_NO_PRIVILEGE);
            return null;
        }
        Player infoPlayer = PlayerManager.I.getPlayer(info.playerUid);
        if (infoPlayer == null) {
            Logs.CLUB.warn("%s player:%d玩家不在本圈中", player, info.playerUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_MEMBERJOB_FAIL, ErrorCode.PLAYER_NOT_EXISTS);
            return null;
        }
        club.changeMemberJob(player.getUid(), info.playerUid, info.jobType);
        player.send(CommandId.CLI_NTF_CLUB_SET_MEMBERJOB_OK, null);
        return null;
    }
}
