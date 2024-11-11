package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.xiuxiu.app.protocol.api.GetHundredInfoResp;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.api.ApiManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;

public class GetApiUserLoginInfoHandler extends BaseHttpHandler {
    @Override
    protected void doHandler(HttpExchange httpExchange) throws IOException {
        Logs.API.debug("获取api用户登陆信息");

        GetHundredInfoResp resp = new GetHundredInfoResp();

        try {
            ApiManager.I.getApiUserInfo();
        } catch (Exception e) {
            Logs.API.error(e);
        }

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
