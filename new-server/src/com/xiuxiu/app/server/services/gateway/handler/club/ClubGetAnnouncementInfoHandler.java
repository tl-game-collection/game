package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfAnnouncementInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqGetAnnouncementInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class ClubGetAnnouncementInfoHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqGetAnnouncementInfo info = (PCLIClubReqGetAnnouncementInfo) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null) {
            Logs.GROUP.warn("%s club:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_CHANGE_ANNOUNCEMENT_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        //如果合过圈，并且不是主圈的情况
        if (club.checkIsJoinInMainClub() && !club.checkIsMainClub()) {
            club = ClubManager.I.getClubByUid(club.getFinalClubId());
        }
        PCLIClubNtfAnnouncementInfo respInfo = new PCLIClubNtfAnnouncementInfo();
        respInfo.clubUid = info.clubUid;
        long expireAfter = club.getClubInfo().getAnnouncementExpireAt() - System.currentTimeMillis();
        if (expireAfter >= 0 && !club.getClubInfo().getAnnouncement().isEmpty()) {
            respInfo.content = club.getClubInfo().getAnnouncement();
            respInfo.expireSeconds = expireAfter;
        }
        player.send(CommandId.CLI_NTF_CLUB_GET_ANNOUNCEMENT_OK, respInfo);
        return null;
    }
}
