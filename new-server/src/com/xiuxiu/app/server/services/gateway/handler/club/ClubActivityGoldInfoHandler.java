package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfActivityGoldInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqActivityGoldInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.activity.ClubActivityManager;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class ClubActivityGoldInfoHandler implements Handler {
    
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqActivityGoldInfo info = (PCLIClubReqActivityGoldInfo) request;
        IClub club = ClubManager.I.getClubByUid(info.id);
        if (null == club) {
            Logs.CLUB.warn("%s 亲友圈不存在:%s", player, info);
            player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_GOLD_INFO_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }

        if (!club.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s groupUid:%d 不在群里", player, info.id);
            if(club.getClubType()== EClubType.GOLD){
                player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_GOLD_INFO_FAIL, ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER);
            }else{
                player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_GOLD_INFO_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
            }
            return null;
        }
        ClubMember member = club.getMember(player.getUid());
        PCLIClubNtfActivityGoldInfo resp=ClubActivityManager.I.getActivityGoldInfo(club, member);
        if(resp.data.size()>0){
            player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_GOLD_INFO_OK,resp);
        }else{
            if(club.matchMemberType(EClubJobType.CHIEF, player.getUid())){
                player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_GOLD_INFO_OK, resp);
            }else{
                player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_GOLD_INFO_FAIL, ErrorCode.ACTIVITY_GOLD_NOT_BEGIN);

            }
        }
        return null;
    }
}
