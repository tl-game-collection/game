package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfValueChange;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqExchangeRewardValue;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMemberExt;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubGoldChangeType;
import com.xiuxiu.app.server.club.constant.EClubRVChangeType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

/**
 * 奖励分兑换成竞技分
 * @author MyPC
 *
 */
public class ClubExchangeRewardValueHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqExchangeRewardValue info = (PCLIClubReqExchangeRewardValue) request;

        //check something
        IClub club= ClubManager.I.getClubByUid(info.clubUid);
        if (null == club) {
            Logs.GROUP.warn("%s club不存在:%s", player, info);
            player.send(CommandId.CLI_NTF_CLUB_EXCHANGE_REWARD_VALUE_FALL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }

        if (info.value < 100 || (info.value % 100) > 0){
            player.send(CommandId.CLI_NTF_CLUB_EXCHANGE_REWARD_VALUE_FALL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }

        if (!club.hasRewardValue(player.getUid(), info.value)) {
            player.send(CommandId.CLI_NTF_CLUB_EXCHANGE_REWARD_VALUE_FALL, ErrorCode.REWARD_VALUE_ENOUGH);
            return null;
        }

        //deal
        if (!club.addMemberClubRewardValue(player.getUid(),-Math.abs(info.value),player.getUid(), EClubRVChangeType.EXCHANGE_VALUE_DEC)){
            player.send(CommandId.CLI_NTF_CLUB_EXCHANGE_REWARD_VALUE_FALL, ErrorCode.REWARD_VALUE_ENOUGH);
            return null;
        }

        if (!club.addMemberClubGold(player.getUid(),Math.abs(info.value)/100,player.getUid(), EClubGoldChangeType.EXCHANGE_REWARD_VALUE)){
            if(!club.addMemberClubRewardValue(player.getUid(),Math.abs(info.value),player.getUid(), EClubRVChangeType.EXCHANGE_VALUE_DEC)){
                Logs.CLUB.error("返回奖励分失败 playerUid:%d ,info:%s",player.getUid(),info);
            }
            player.send(CommandId.CLI_NTF_CLUB_EXCHANGE_REWARD_VALUE_FALL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }

        PCLIClubNtfValueChange result=new PCLIClubNtfValueChange();
        result.clubUid=club.getClubUid();
        result.pUid=player.getUid();
        ClubMemberExt clubMemberExt = club.getMemberExt(player.getUid(),true);
        result.gold = clubMemberExt.getGold();
        result.rv = clubMemberExt.getRewardValue();
        player.send(CommandId.CLI_NTF_CLUB_EXCHANGE_REWARD_VALUE_OK, result);
        return null;
    }
}
