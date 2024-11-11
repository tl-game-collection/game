package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqClubInviteJoin;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.JsonUtil;

import java.util.List;

/**
 *
 */
public class ClubInviteJoinHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqClubInviteJoin info = (PCLIClubReqClubInviteJoin) request;
        IClub iClub = ClubManager.I.getClubByUid(info.clubUid);
        if (null == iClub) {
            Logs.CLUB.warn("%s clubUid:%d 俱乐部不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_INVITE_JION_CLUB_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if(!iClub.hasMember(player.getUid())){
            Logs.CLUB.warn("%s clubUid:%d 玩家不在俱乐部中", player, info.clubUid);
            ErrorCode ec = iClub.getClubType() == EClubType.CARD ? ErrorCode.CLUB_NOT_HAVE_PLAYER : ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER;
            player.send(CommandId.CLI_NTF_CLUB_INVITE_JION_CLUB_FAIL, ec);
            return null;
        }
        Player invitor = PlayerManager.I.getPlayer(info.invitorUid);
        if (null == invitor) {
            Logs.CLUB.warn("%s invitorUid:%d 不存在", player, info.invitorUid);
            player.send(CommandId.CLI_NTF_CLUB_INVITE_JION_CLUB_FAIL, ErrorCode.PLAYER_NOT_EXISTS);
            return null;
        }
        if(iClub.hasMember(info.invitorUid)){
            Logs.CLUB.warn("%s clubUid:%d 玩家已在俱乐部中", player, info.clubUid);
            ErrorCode ec = iClub.getClubType() == EClubType.CARD ? ErrorCode.CLUB_CARD_ALREADY_IN : ErrorCode.CLUB_GOLD_ALREADY_IN;
            player.send(CommandId.CLI_NTF_CLUB_INVITE_JION_CLUB_FAIL, ec);
            return null;
        }
        List<Long> groupIds = JsonUtil.fromJson2List(player.getClubUids(), Long.class);
        if (groupIds.size() == 0 || player.getRecommendInfo().getRecommendPlayerUid() == -1) {
            if (!ClubManager.I.lock(player.getUid())) {
                Logs.CLUB.warn("%s clubUid:%d 正在操作", player, info.clubUid);
                player.send(CommandId.CLI_NTF_CLUB_INVITE_JION_CLUB_FAIL, ErrorCode.REQUEST_INVALID_DATA);
                return null;
            }
            try {
                iClub.addMember(info.invitorUid, player, EClubJobType.NORMAL);
                player.send(CommandId.CLI_NTF_CLUB_INVITE_JION_CLUB_OK, null);
                return null;
            } finally {
                ClubManager.I.unlock(player.getUid());
            }
        }
        return null;
    }
}
