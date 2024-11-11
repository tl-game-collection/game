package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfGetClubInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqGetClubInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class ClubGetClubInfoHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqGetClubInfo info = (PCLIClubReqGetClubInfo) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null) {
            Logs.GROUP.warn("%s club:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_GET_CLUBINFO_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        PCLIClubNtfGetClubInfo respInfo = new PCLIClubNtfGetClubInfo();
        respInfo.clubUid = info.clubUid;
        respInfo.parentUid = club.getClubInfo().getParentUid();
        respInfo.childUidList.addAll(club.getClubInfo().getChildUid());
        player.send(CommandId.CLI_NTF_CLUB_GET_CLUBINFO_OK, respInfo);
        return null;
    }
}
