package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqActivityDivideChange;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.activity.ClubActivityManager;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

/**
 * 修改奖励分成获取比例
 * 
 * @author Administrator
 *
 */
public class ClubActivityDivideChangeHandler implements Handler {
    
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqActivityDivideChange info = (PCLIClubReqActivityDivideChange) request;
        if (null == info.base || null == info.items || info.items.size() == 0) {
            player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_DIVIDE_CHANGE_FAIL, ErrorCode.REQUEST_INVALID);
            return null;
        }

        IClub club = ClubManager.I.getClubByUid(info.id);
        if (null == club) {
            Logs.CLUB.warn("%s clubUid:%d 群不存在", player, info.id);
            player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_DIVIDE_CHANGE_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (!club.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s groupUid:%d 不在群里", player, info.id);
            if(club.getClubType()== EClubType.GOLD){
                player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_DIVIDE_CHANGE_FAIL, ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER);
            }else{
                player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_DIVIDE_CHANGE_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
            }
            return null;
        }
        if (!club.matchMemberType(EClubJobType.CHIEF, player.getUid())) {
            Logs.CLUB.warn("%s 没有修改奖励分成获取比例的权限", player);
            player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_DIVIDE_CHANGE_FAIL,
                    ErrorCode.GROUP_NOT_PRIVILEGE_MANAGER_SERVICE_CHARGE);
            return null;
        }

        if (!ClubManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s info:%s 正在操作", player, info);
            player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_DIVIDE_CHANGE_FAIL, ErrorCode.REPEAT_OPERATE);
            return null;
        }
        try {
            ClubActivityManager.I.changeByClub(info.id, info.base, info.items,info.open);
            player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_DIVIDE_CHANGE_OK, null);
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
        return null;
    }
}