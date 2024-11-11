package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubBriefInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqSearchInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;

/**
 *
 */
public class ClubSearchHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqSearchInfo info = (PCLIClubReqSearchInfo) request;
        IClub iClub = ClubManager.I.getClubByUid(info.clubUid);
        if (null == iClub) {
            Logs.CLUB.warn("%s clubUid:%d 搜索的俱乐部不存在", player, info.clubUid);
            ErrorCode ec = info.clubType == EClubType.CARD.getType() ? ErrorCode.CLUB_CARD_NOT_EXISTS : ErrorCode.CLUB_GOLD_NOT_EXISTS;
            player.send(CommandId.CLI_NTF_CLUB_SEARCH_FAIL, ec);
            return null;
        }
        if (iClub.getClubType().getType() != info.clubType) {
            Logs.CLUB.warn("%s clubUid:%d 搜索的俱乐部类型不对", player, info.clubUid);
            ErrorCode ec = info.clubType == EClubType.CARD.getType() ? ErrorCode.CLUB_CARD_NOT_EXISTS : ErrorCode.CLUB_GOLD_NOT_EXISTS;
            player.send(CommandId.CLI_NTF_CLUB_SEARCH_FAIL, ec);
            return null;
        }
        if (!ClubManager.I.lock(player.getUid())) {
            Logs.CLUB.warn("%s clubUid:%d 正在操作", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_SEARCH_FAIL, ErrorCode.REPEAT_OPERATE);
            return null;
        }
        try {
            PCLIClubBriefInfo briefInfo = new PCLIClubBriefInfo();
            briefInfo.desc = iClub.getDesc();
            briefInfo.gameDesc = iClub.getGameDesc();
            briefInfo.name = iClub.getName();
            briefInfo.icon = iClub.getIcon();
            briefInfo.uid = iClub.getClubUid();
            briefInfo.clubType = iClub.getClubType().getType();
            briefInfo.createTime = iClub.getCreateTime();
            Player founderPlayer = PlayerManager.I.getPlayer(iClub.getOwnerId());
            briefInfo.founder = founderPlayer.getPlayerSmallInfo();
            player.send(CommandId.CLI_NTF_CLUB_SEARCH_OK, briefInfo);
            return null;
        } finally {
            ClubManager.I.unlock(player.getUid());
        }
    }
}
