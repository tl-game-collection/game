package com.xiuxiu.app.server.services.api.club;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.club.GetClubMemberGold;
import com.xiuxiu.app.protocol.api.temp.club.GetClubMemberGoldResp;
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

import java.io.IOException;

/**
 * @auther: luocheng
 * @date: 2019/12/27 10:40
 */
public class GetClubMemberGoldHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Logs.API.debug("获取群玩家竞技分");
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        GetClubMemberGold info = JsonUtil.fromJson(body, GetClubMemberGold.class);
        String sign = MD5Util.getMD5(info.clubUid, info.playerUid, Config.APP_KEY);
        GetClubMemberGoldResp resp = new GetClubMemberGoldResp();
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            IClub club = ClubManager.I.getClubByUid(info.clubUid);
            if (club == null) {
                resp.ret = ErrorCode.CLUB_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.CLUB_NOT_EXISTS.getMsg();
                break;
            }
            Player player = PlayerManager.I.getPlayer(info.playerUid);
            if (player == null) {
                resp.ret = ErrorCode.PLAYER_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.PLAYER_NOT_EXISTS.getMsg();
                break;
            }
            if (!club.hasMember(info.playerUid)) {
                resp.ret = ErrorCode.CLUB_NOT_HAVE_PLAYER.getRet();
                resp.msg = ErrorCode.CLUB_NOT_HAVE_PLAYER.getMsg();
                break;
            }
            resp.clubUid = info.clubUid;
            resp.clubName = club.getName();
            resp.playerUid = info.playerUid;
            resp.playerName = player.getName();
            resp.upLineUid = club.getMember(info.playerUid).getUplinePlayerUid();
            Player uplinePlayer = PlayerManager.I.getPlayer(resp.upLineUid);
            if (uplinePlayer != null) {
                resp.upLineName = uplinePlayer.getName();
            }
            resp.gold = club.getMemberExt(info.playerUid,true).getGold();
        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
