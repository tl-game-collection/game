package com.xiuxiu.app.server.services.api.club;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.player.GetClubMemberDownLine;
import com.xiuxiu.app.protocol.api.temp.player.GetClubMemberDownLineResp;
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

/**
 * 查询群管理下线成员
 * @author MyPC
 *
 */
public class GetClubMemberDownLineHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Logs.API.debug("收到查询群管理下线成员请求");
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        GetClubMemberDownLine info = JsonUtil.fromJson(body, GetClubMemberDownLine.class);
        String sign = MD5Util.getMD5(info.clubUid, info.managerUid, Config.APP_KEY);
        GetClubMemberDownLineResp resp = new GetClubMemberDownLineResp();
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            IClub club = ClubManager.I.getClubByUid(info.clubUid);
            if (club == null) {
                resp.ret = ErrorCode.GROUP_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.GROUP_NOT_EXISTS.getMsg();
                break;
            }
            Player player = PlayerManager.I.getPlayer(info.managerUid);
            if (null == player) {
                resp.ret = ErrorCode.PLAYER_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.PLAYER_NOT_EXISTS.getMsg();
                break;
            }
            if (!club.hasMember(info.managerUid)) {
                resp.ret = ErrorCode.GROUP_NOT_IN.getRet();
                resp.msg = ErrorCode.GROUP_NOT_IN.getMsg();
                break;
            }
            for (Long tempUid : club.getAllMemberUids()) {
                ClubMember tempMember = club.getMember(tempUid);
                if (tempMember == null) {
                    continue;
                }
                if (tempMember.getUplinePlayerUid() == info.managerUid) {
                    Player tempPlayer = PlayerManager.I.getPlayer(tempUid);
                    if (tempPlayer == null) {
                        continue;
                    }
                    GetClubMemberDownLineResp.MemberInfo memberInfo = new GetClubMemberDownLineResp.MemberInfo();
                    memberInfo.avatar = tempPlayer.getIcon();
                    memberInfo.nickName = tempPlayer.getName();
                    memberInfo.playerUid = tempUid;
                    memberInfo.upLinePlayerUid = info.managerUid;
                    resp.data.add(memberInfo);
                }
            }
        } while (false);
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
