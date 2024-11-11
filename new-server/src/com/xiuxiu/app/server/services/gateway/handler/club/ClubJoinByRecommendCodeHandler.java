package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqJoinByRecommendCode;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.uniquecode.UniqueCodeManager;
import com.xiuxiu.core.KeyValue;
import com.xiuxiu.core.net.message.Handler;

public class ClubJoinByRecommendCodeHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqJoinByRecommendCode info = (PCLIClubReqJoinByRecommendCode) request;

        //check something
        KeyValue<Long,Long> data = UniqueCodeManager.I.getGroupRecommendParamByCode(info.code);
        if (null == data){
            player.send(CommandId.CLI_NTF_CLUB_JOIN_BY_RECOMMEND_CODE_FAIL, ErrorCode.CLUB_RECOMMEND_CODE_NOT_EXIST);
            return null;
        }

        long clubUid = data.getKey();
        long invitorPlayerUid = data.getValue();
        if (invitorPlayerUid == player.getUid()){
            player.send(CommandId.CLI_NTF_CLUB_JOIN_BY_RECOMMEND_CODE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }

        IClub club = ClubManager.I.getClubByUid(clubUid);
        if (null == club) {
            Logs.CLUB.warn("%s clubUid:%d 俱乐部不存在", player, clubUid);
            player.send(CommandId.CLI_NTF_CLUB_JOIN_BY_RECOMMEND_CODE_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }

        club.addMember(invitorPlayerUid, player, EClubJobType.NORMAL);
        player.send(CommandId.CLI_NTF_CLUB_JOIN_BY_RECOMMEND_CODE_OK, null);
        return null;

    }
}
