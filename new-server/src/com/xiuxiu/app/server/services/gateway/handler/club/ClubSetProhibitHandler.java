package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfSetProhibit;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqSetProhibit;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubMemberStateType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

import java.util.List;
import java.util.Map;

public class ClubSetProhibitHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqSetProhibit info = (PCLIClubReqSetProhibit) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null) {
            Logs.CLUB.warn("%s club:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_PROHIBIT_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        //判断权限
        boolean bCanSet = false;//是否有权限设置
        //本圈圈主和副圈主可以设置本圈玩家
        if (club.hasMember(player.getUid()) && club.hasMember(info.playerUid)) {
            //圈主
            if (club.getOwnerId() == player.getUid()) {
                bCanSet = true;
            }
            //副圈主
            if (club.getMember(player.getUid()).checkJobType(EClubJobType.DEPUTY)) {
                bCanSet = true;
            }
        }

        //合圈后
        if (club.checkIsJoinInMainClub()) {
            IClub rootClub = club;
            if (!club.checkIsMainClub()) {
                rootClub = ClubManager.I.getClubByUid(club.getFinalClubId());
            }
            //盟主可以设置所有人
            if (rootClub.getOwnerId() == player.getUid()) {
                bCanSet = true;
            } else {
                //管理员可以设置管辖的圈内玩家
                for (Map.Entry<Long,List<Long>> entry : rootClub.getClubInfo().getManagerInfo().entrySet()) {
                    if (entry.getKey() == player.getUid() && entry.getValue().contains(info.clubUid)) {
                        bCanSet = true;
                        break;
                    }
                }
            }
        }
        if (!bCanSet) {
            Logs.CLUB.warn("%s 没有权限", player);
            player.send(CommandId.CLI_NTF_CLUB_SET_PROHIBIT_FAIL, ErrorCode.CLUB_NO_PRIVILEGE);
            return null;
        }

        PCLIClubNtfSetProhibit respInfo = new PCLIClubNtfSetProhibit();
        respInfo.clubUid = info.clubUid;
        respInfo.playerUid = info.playerUid;
        respInfo.isProhibit = info.isProhibit;
        player.send(CommandId.CLI_NTF_CLUB_SET_PROHIBIT_OK, respInfo);

        club.changeMemberState(player.getUid(), info.playerUid, info.isProhibit ? EClubMemberStateType.FORBID.ordinal() : EClubMemberStateType.ORMAL.ordinal());
        return null;
    }
}
