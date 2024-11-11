package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfGetRecommendCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqGetRecommendCode;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMemberExt;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.uniquecode.UniqueCodeManager;
import com.xiuxiu.core.net.message.Handler;

public class ClubGetRecommendCodeHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqGetRecommendCode info = (PCLIClubReqGetRecommendCode) request;

        //check something
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (null == club || !club.hasMember(player.getUid())){
            player.send(CommandId.CLI_NTF_CLUB_GET_RECOMMEND_CODE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }

        ClubMemberExt clubMemberExt = club.getMemberExt(player.getUid(),true);
        if (null == clubMemberExt){
            player.send(CommandId.CLI_NTF_CLUB_GET_RECOMMEND_CODE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }

        //deal
        long code = 0;
        if (clubMemberExt.getCode() <= 0){
            code = UniqueCodeManager.I.makeGroupRecommendCode(player.getUid(),club.getClubUid());
            if (code > 0){
                clubMemberExt.setCode(code);
                clubMemberExt.setDirty(true);
            }
        }

        if (code <= 0){
            player.send(CommandId.CLI_NTF_CLUB_GET_RECOMMEND_CODE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }

        PCLIClubNtfGetRecommendCode result = new PCLIClubNtfGetRecommendCode();
        result.clubUid = club.getClubUid();
        result.code = code;
        player.send(CommandId.CLI_NTF_CLUB_GET_RECOMMEND_CODE_OK,result);
        return null;
    }
}
