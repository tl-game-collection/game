package com.xiuxiu.app.server.services.gateway.handler.club;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.club.PCLIClubNtfSetClubInfo;
import com.xiuxiu.app.protocol.client.club.PCLIClubReqSetClubInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;


public class ClubSetClubInfoHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIClubReqSetClubInfo info = (PCLIClubReqSetClubInfo) request;
        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null) {
            Logs.GROUP.warn("%s club:%d不存在", player, info.clubUid);
            player.send(CommandId.CLI_NTF_CLUB_SET_CLUBINFO_FAIL, ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS);
            return null;
        }
        if (ClubManager.I.isExistName(club.getClubType(), info.name) || 0 != DBManager.I.getClubInfoDAO().isExistName(club.getClubType().getType(), info.name)) {
            Logs.CLUB.warn("%s 俱乐部名称有相同, 无法创建俱乐部", player);
            ErrorCode ec = club.getClubType().getType() == EClubType.CARD.getType() ? ErrorCode.PLAYER_GROUP_NAME_REPETITION : ErrorCode.PLAYER_GOLD_NAME_REPETITION;
            player.send(CommandId.CLI_NTF_CLUB_SET_CLUBINFO_FAIL, ec);
            return null;
        }
        player.send(CommandId.CLI_NTF_CLUB_SET_CLUBINFO_OK,null);

        PCLIClubNtfSetClubInfo respInfo = new PCLIClubNtfSetClubInfo();
        respInfo.clubUid = info.clubUid;
        respInfo.name = info.name;
        respInfo.desc = info.desc;
        respInfo.icon = info.icon;
        respInfo.gameDesc = info.gameDesc;
        club.broadcast(CommandId.CLI_NTF_CLUB_CHANGECLUBINFO, respInfo);

        club.getClubInfo().setName(info.name);
        club.getClubInfo().setDesc(info.desc);
        club.getClubInfo().setIcon(info.icon);
        club.getClubInfo().setGameDesc(info.gameDesc);
        club.getClubInfo().setDirty(true);

        return null;
    }
}
