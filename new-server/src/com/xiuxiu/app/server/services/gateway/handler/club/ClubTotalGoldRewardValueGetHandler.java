package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfTotalGoldRewardValueGet;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqTotalGoldRewardValueGet;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

/**
 *
 */
public class ClubTotalGoldRewardValueGetHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqTotalGoldRewardValueGet info = (PCLIClubReqTotalGoldRewardValueGet) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null) {
            Logs.CLUB.warn("%s club:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_TOTAL_GOLD_REWARD_VALUE_GET_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        IClub finalClub = ClubManager.I.getClubByUid(club.getFinalClubId());
        if (null == finalClub) {
            Logs.CLUB.warn("%s club:%d 主圈不存在 ", player, info.clubUid);
            ErrorCode ec = club.getClubType() == EClubType.CARD ? ErrorCode.CLUB_CARD_NOT_EXISTS : ErrorCode.CLUB_GOLD_NOT_EXISTS;
            player.send(CommandId.CLI_NTF_CLUB_TOTAL_GOLD_REWARD_VALUE_GET_FAIL, ec);
            return null;
        }
        if(player.getUid() != finalClub.getOwnerId()){
            Logs.CLUB.warn("%s clubUid:%d 不是圈主没有权限", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_TOTAL_GOLD_REWARD_VALUE_GET_FAIL, ErrorCode.CLUB_NOT_CHIEF_NO_PRIVILEGE);
            return null;
        }
        if (!ClubManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s info:%s 正在操作", player, info);
            player.send(CommandId.CLI_NTF_CLUB_TOTAL_GOLD_REWARD_VALUE_GET_FAIL, ErrorCode.CLUB_IN_LOCK);
            return null;
        }
        try {
            PCLIClubNtfTotalGoldRewardValueGet valueGet = new PCLIClubNtfTotalGoldRewardValueGet();
            valueGet.clubUid = info.clubUid;
            valueGet.name = info.name;
            valueGet.rewardValue = club.getTotalGoldAndRewardValue()[0];
            valueGet.totalGold = club.getTotalGoldAndRewardValue()[1];
            player.send(CommandId.CLI_NTF_CLUB_TOTAL_GOLD_REWARD_VALUE_GET_OK, valueGet);
            return null;
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
    }
}
