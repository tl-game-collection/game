package com.xiuxiu.app.server.services.api.club;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.club.GetClubMemberRelationList;
import com.xiuxiu.app.protocol.api.temp.club.GetClubMemberRelationListResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

public class GetClubMemberRelationListHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        GetClubMemberRelationList info = JsonUtil.fromJson(body, GetClubMemberRelationList.class);
        Logs.API.debug("根据群id查询群成员列表:%s", info);
        String sign = MD5Util.getMD5(info.clubUid, Config.APP_KEY);
        GetClubMemberRelationListResp resp = new GetClubMemberRelationListResp();
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            IClub club = ClubManager.I.getClubByUid(info.clubUid);
            if (null == club) {
                resp.ret = ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS.getMsg();
                break;
            }
            // 查询单个群
            resp.data = new GetClubMemberRelationListResp.ClubInfo();
            resp.data.clubUid = club.getClubUid();
            resp.data.clubName = club.getName();
            resp.data.clubIcon = club.getIcon();
            club.foreach(members -> {
                GetClubMemberRelationListResp.ClubMember clubMember = new GetClubMemberRelationListResp.ClubMember();
                clubMember.playerUid = members[0].getPlayerUid();
                Player tempPlayer = PlayerManager.I.getPlayer(members[0].getPlayerUid());
                if (null != tempPlayer) {
                    clubMember.uplinePlayerUid = members[0].getUplinePlayerUid();
                    clubMember.memberType = members[0].getJobType();
                    clubMember.nickname = tempPlayer.getName();
                    clubMember.icon = tempPlayer.getIcon();
                }
                resp.data.members.add(clubMember);
            });
        } while (false);
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
