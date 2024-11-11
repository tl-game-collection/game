package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.BindBizChannelInfo;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;

public class BindBizChannel extends BaseHttpHandler{
    @Override
    protected void doHandler(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        BindBizChannelInfo info = JsonUtil.fromJson(body, BindBizChannelInfo.class);
        Logs.API.debug("绑定业务渠道到群:%s", info);

        ErrorMsg resp = new ErrorMsg();

        do {
            if (null == ClubManager.I.getClubByUid(info.groupUid)) {
                resp.setRet(ErrorCode.GROUP_NOT_EXISTS);
                break;
            }
            Config.bizChannel2GroupUid.put(info.bizChannel, info.groupUid);
        } while(false);
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
