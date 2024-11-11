package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqActivityGoldRemove;
import com.xiuxiu.app.protocol.client.club.PCLIGroupNtfQuestDel;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.activity.ClubActivityManager;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class ClubActivityGoldRemoveHandler implements Handler {

    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqActivityGoldRemove info = (PCLIClubReqActivityGoldRemove) request;
        IClub club = ClubManager.I.getClubByUid(info.id);
        if (null == club) {
            Logs.CLUB.warn("%s 亲友圈不存在:%s", player, info);
            player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_GOLD_REMOVE_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (!club.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s 没有权限查看联盟:%d成员列表", player, info.id);
            if(club.getClubType()== EClubType.GOLD){
                player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_GOLD_REMOVE_FAIL, ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER);
            }else{
                player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_GOLD_REMOVE_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
            }
            return null;
        }
        if (!club.matchMemberType(EClubJobType.CHIEF, player.getUid())) {
            Logs.CLUB.warn("%s 没有修改联盟奖励分成获取比例的权限", player);
            player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_GOLD_REMOVE_FAIL,
                    ErrorCode.GROUP_NOT_PRIVILEGE_MANAGER_SERVICE_CHARGE);
            return null;
        }
        if (!ClubManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s clubUid:%d 正在操作", player, info.id);
            player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_GOLD_REMOVE_FAIL, ErrorCode.REPEAT_OPERATE);
            return null;
        }
        try {
            if (ClubActivityManager.I.removeActivityGold(club, info.boxUid)) {
                PCLIGroupNtfQuestDel result = new PCLIGroupNtfQuestDel();
                result.boxUid = info.boxUid;
                club.broadcastToAllClub(CommandId.CLI_NTF_CLUB_ACTIVITY_GOLD_REMOVE_OK,result);
                //player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_GOLD_REMOVE_OK, result);
            }
            return null;
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
    }
}