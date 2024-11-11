package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.xiuxiu.app.protocol.api.UnBindBizChannelInfo;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;

public class UnBindBizChannel extends BaseHttpHandler{
    @Override
    protected void doHandler(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        UnBindBizChannelInfo info = JsonUtil.fromJson(body, UnBindBizChannelInfo.class);
        Logs.API.debug("解绑定业务渠道到群:%s", info);

        ErrorMsg resp = new ErrorMsg();

        Config.bizChannel2GroupUid.remove(info.bizChannel);
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
