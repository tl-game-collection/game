package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIPlayerGroupReqQuestGetReward;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.activity.ClubActivityManager;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class ClubActivityGoldRewardHandler implements Handler {

    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerGroupReqQuestGetReward info = (PCLIPlayerGroupReqQuestGetReward) request;
        if (0 == info.clubUid) {
            Logs.CLUB.warn("%s 群id不能为空", info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_GOLD_REWARD_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (null == club) {
            Logs.CLUB.warn("%s 群id不存在", info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_GOLD_REWARD_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (!club.hasMember(player.getUid())) {
            Logs.CLUB.warn("%s 群id不存在", info.clubUid);
            if(club.getClubType()== EClubType.GOLD){
                player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_GOLD_REWARD_FAIL, ErrorCode.CLUB_GOLD_NOT_HAVE_PLAYER);
            }else{
                player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_GOLD_REWARD_FAIL, ErrorCode.CLUB_NOT_HAVE_PLAYER);
            }
            return null;
        }

        if (!ClubManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s groupUid:%d 正在操作", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_GOLD_REWARD_FAIL, ErrorCode.REPEAT_OPERATE);
            return null;
        }
        try {
            ErrorCode errorCode = ClubActivityManager.I.rewardActivityGold(player, club, info.boxUid, info.index);
            if (errorCode == ErrorCode.OK) {
                player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_GOLD_REWARD_OK, null);
            }else {
                player.send(CommandId.CLI_NTF_CLUB_ACTIVITY_GOLD_REWARD_FAIL, errorCode);
            }
            return null;
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
    }
}
