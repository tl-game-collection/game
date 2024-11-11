package com.xiuxiu.app.server.services.api.club;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.club.GetClubMemberUpLines;
import com.xiuxiu.app.protocol.api.temp.club.GetClubMemberUpLinesResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

import java.io.IOException;
import java.util.List;

/**
 * @auther: luocheng
 * @date: 2020/1/5 10:32
 */
public class GetClubMemberUpLinesHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Logs.API.debug("获取玩家上线一条线");
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        GetClubMemberUpLines info = JsonUtil.fromJson(body, GetClubMemberUpLines.class);
        String sign = MD5Util.getMD5(info.clubUid, info.playerUid, Config.APP_KEY);
        GetClubMemberUpLinesResp resp = new GetClubMemberUpLinesResp();
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
            resp.playerUid = info.playerUid;
            int count = 0;//递归层级，以防死递归
            this.getUpline(club.getMember(info.playerUid), club, resp.upLines , count);
        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }

    private void getUpline(ClubMember _clubMember, IClub _club, List<Long> _upLines, int _count) {
        if (_count >= 1000) {
            Logs.API.error("获取玩家上线一条线递归层级超过1000 %d", _count);
            return;
        }
        _count++;
        long uplineUid = _clubMember.getUplinePlayerUid();
        if (uplineUid > 0 && _club.hasMember(uplineUid) && !_upLines.contains(uplineUid)) {
            _upLines.add(uplineUid);
            this.getUpline(_club.getMember(uplineUid), _club, _upLines, _count);
        }
    }
}
