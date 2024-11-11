package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfSetOnlyUpLineSetGoldNotice;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqSetGoldUpLine;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.ICallback;
import com.xiuxiu.core.net.message.Handler;

public class ClubSetOnlyUpLineSetGoldHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqSetGoldUpLine info = (PCLIClubReqSetGoldUpLine) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (null == club) {
            Logs.CLUB.warn("%s CLUB_UID:%d 群不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_ONLYUPLINESETGOLD_FAIL, ErrorCode.CLUB_GOLD_NOT_EXISTS);
            return null;
        }

        if (!club.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s 不在群:%d里, 无法站起", player, info.clubUid);
            if (club.getClubType() == EClubType.GOLD) {
                player.send(CommandId.CLI_NTF_CLUB_ONLYUPLINESETGOLD_FAIL, ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER);
            } else {
                player.send(CommandId.CLI_NTF_CLUB_ONLYUPLINESETGOLD_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
            }
            return null;
        }

        Player otherPlayer = PlayerManager.I.getPlayer(info.uid);
        if (null == otherPlayer) {
            Logs.CLUB.warn("%s playerUid:%d 玩家不存在", player, info.uid);
            player.send(CommandId.CLI_NTF_CLUB_ONLYUPLINESETGOLD_FAIL, ErrorCode.PLAYER_NOT_EXISTS);
            return null;
        }

        ClubMember otherMember = club.getMember(info.uid);
        if (otherMember == null) {
            Logs.CLUB.warn("%s playerUid:%d 玩家不存在", player, info.uid);
            player.send(CommandId.CLI_NTF_CLUB_ONLYUPLINESETGOLD_FAIL, ErrorCode.PLAYER_NOT_EXISTS);
            return null;
        }

        // 是否是目标玩家的直属上级
        if (otherMember.getUplinePlayerUid() != player.getUid()) {
            player.send(CommandId.CLI_NTF_CLUB_ONLYUPLINESETGOLD_FAIL, ErrorCode.GM_INVALID_OPERATE);
            return null;
        }
        if (!ClubManager.I.lock(player.getUid())) {
            player.send(CommandId.CLI_NTF_CLUB_ONLYUPLINESETGOLD_FAIL, ErrorCode.CLUB_IN_LOCK);
            return null;
        }
        try {
            PCLIClubNtfSetOnlyUpLineSetGoldNotice notice = new PCLIClubNtfSetOnlyUpLineSetGoldNotice();
            otherMember.setOnlyUpLineSetGold(info.flag ? 1 : 0);
            otherMember.setDirty(Boolean.TRUE);
            notice.uid = info.uid;
            notice.flag = info.flag;
            notice.clubUid = info.clubUid;

            club.foreach(new ICallback<ClubMember>() {
                @Override
                public void call(ClubMember... member) {
                    ClubMember tempClubMember = member[0];
                    if (tempClubMember.checkJobType(EClubJobType.CHIEF)
                            || tempClubMember.checkJobType(EClubJobType.DEPUTY)
                            || tempClubMember.getPlayerUid() == player.getUid()
                            || tempClubMember.getUplinePlayerUid() == player.getUid()) {
                        Player tempPlayer = PlayerManager.I.getOnlinePlayer(tempClubMember.getPlayerUid());
                        if (tempPlayer != null) {
                            tempPlayer.send(CommandId.CLI_NTF_CLUB_ONLYUPLINESETGOLD_NOTICE, notice);
                        }
                    }
                }
            });
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
        return null;
    }

}
