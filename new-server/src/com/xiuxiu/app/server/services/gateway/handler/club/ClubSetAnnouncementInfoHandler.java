package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfChangeAnnouncementInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqChangeAnnouncementInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubJobType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

import java.util.ArrayList;
import java.util.List;

public class ClubSetAnnouncementInfoHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqChangeAnnouncementInfo info = (PCLIClubReqChangeAnnouncementInfo) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null) {
            Logs.CLUB.warn("%s club:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_CHANGE_ANNOUNCEMENT_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (info.expireSeconds < 0) {
            Logs.GROUP.warn("%s clubUid:%d, expireSeconds:%d 参数错误", player, info.clubUid, info.expireSeconds);
            player.send(CommandId.CLI_NTF_CLUB_CHANGE_ANNOUNCEMENT_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        //合过圈
        if (club.checkIsJoinInMainClub()) {
            IClub rootClub = ClubManager.I.getClubByUid(club.getFinalClubId());
            boolean isCanSet = false;//是否能发公告
            //总圈主可以发
            if (rootClub.getOwnerId() == player.getUid()) {
                isCanSet = true;
            }
            //管理员可以发
            if (rootClub.getClubInfo().getManagerInfo().containsKey(player.getUid())) {
                isCanSet = true;
            }
            List<Long> allClubUid = new ArrayList<>();
            rootClub.fillDepthChildClubUidList(allClubUid);
            //各个子圈圈主可以发
            for (Long m_clubUid : allClubUid) {
                IClub tempClub = ClubManager.I.getClubByUid(m_clubUid);
                if (tempClub == null) {
                    continue;
                }
                if (tempClub.getOwnerId() == player.getUid()) {
                    isCanSet = true;
                }
            }
            if (!isCanSet) {
                Logs.CLUB.warn("%s club:%d没有权限", player, info.clubUid);
                player.send(CommandId.CLI_NTF_CLUB_CHANGE_ANNOUNCEMENT_FAIL, ErrorCode.CLUB_NO_PRIVILEGE);
                return null;
            }
            rootClub.changeAnnouncement(info.content,info.expireSeconds);
            for (Long m_clubUid : allClubUid) {
                IClub tempClub = ClubManager.I.getClubByUid(m_clubUid);
                if (tempClub == null) {
                    continue;
                }
                tempClub.changeAnnouncement(info.content,info.expireSeconds);
            }
        } else {
            //不是圈主和副圈主不能发公告
            if (player.getUid() != club.getOwnerId() && !club.getMember(player.getUid()).checkJobType(EClubJobType.DEPUTY)) {
                Logs.CLUB.warn("%s club:%d没有权限", player, info.clubUid);
                player.send(CommandId.CLI_NTF_CLUB_CHANGE_ANNOUNCEMENT_FAIL, ErrorCode.CLUB_NO_PRIVILEGE);
                return null;
            }
            club.changeAnnouncement(info.content,info.expireSeconds);
        }
        PCLIClubNtfChangeAnnouncementInfo cementInfo = new PCLIClubNtfChangeAnnouncementInfo();
        cementInfo.clubUid = info.clubUid;
        cementInfo.content = info.content;
        cementInfo.expireSeconds = info.expireSeconds;
        player.send(CommandId.CLI_NTF_CLUB_CHANGE_ANNOUNCEMENT_OK, cementInfo);
        return null;
    }
}
