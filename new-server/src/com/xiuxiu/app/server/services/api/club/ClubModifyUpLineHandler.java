package com.xiuxiu.app.server.services.api.club;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.club.ClubModifyUpLineReq;
import com.xiuxiu.app.protocol.api.temp.club.ClubModifyUpLineResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

import java.io.IOException;

public class ClubModifyUpLineHandler  implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        ClubModifyUpLineReq info = JsonUtil.fromJson(body, ClubModifyUpLineReq.class);

        Logs.API.warn("修改群玩家上级:%s", info);

        //check something
        ClubModifyUpLineResp resp = new ClubModifyUpLineResp();

        String sign = MD5Util.getMD5(info.clubUid,info.playerUid,info.upLineUid,Config.APP_KEY);
        if (null == sign || !sign.equalsIgnoreCase(info.sign)) {
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            sendFailToClient(httpExchange,resp);
            return;
        }

        if (0 >= info.clubUid || 0 >= info.playerUid || 0 >= info.upLineUid){
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            sendFailToClient(httpExchange,resp);
            return;
        }

        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null) {
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            sendFailToClient(httpExchange,resp);
            return;
        }

        ClubMember clubMember = club.getMember(info.playerUid);
        if (null == clubMember){
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            sendFailToClient(httpExchange,resp);
            return;
        }

        //deal
        club.setMemberUpLine(clubMember,club.getMember(info.upLineUid));
        clubMember.save();

        resp.ret = ErrorCode.OK.getRet();
        resp.msg = ErrorCode.OK.getMsg();
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }

    private void sendFailToClient(HttpExchange httpExchange, ClubModifyUpLineResp resp) throws IOException {
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
