package com.xiuxiu.app.server.services.api.player;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.club.ClubMemberManager;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

import java.io.IOException;

/**
 * 管理群成员信息
 */
public class ClubMemberManagertHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Logs.API.debug("收到管理群成员信息请求");
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        ClubMemberManager info = JsonUtil.fromJson(body, ClubMemberManager.class);
        String sign = MD5Util.getMD5(info.playerUid, info.clubUid, info.targetPlayerUid, info.name, info.icon, Config.APP_KEY);
        ErrorMsg resp = new ErrorMsg();
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            if(info.clubUid>0) {
            	IClub iClub = ClubManager.I.getClubByUid(info.clubUid);
                if (iClub == null) {
                    resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                    resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                    break;
                }
                if (iClub.getOwnerId() != info.playerUid) {
                    resp.ret = ErrorCode.CLUB_NO_PRIVILEGE.getRet();
                    resp.msg = ErrorCode.CLUB_NO_PRIVILEGE.getMsg();
                    break;
                }
            }
            Player targetPlayer = PlayerManager.I.getPlayer(info.targetPlayerUid);
            if (targetPlayer == null) {
                resp.ret = ErrorCode.CLUB_NOT_HAVE_PLAYER.getRet();
                resp.msg = ErrorCode.CLUB_NOT_HAVE_PLAYER.getMsg();
                break;
            }
            if(!info.name.equals("")) {
            	targetPlayer.setName(info.name);
            }
            if(!info.icon.equals("")) {
            	targetPlayer.setIcon(info.icon);
            }
            targetPlayer.setDirty(true);
            targetPlayer.save();

            resp.ret = ErrorCode.OK.getRet();
            resp.msg = ErrorCode.OK.getMsg();
        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
