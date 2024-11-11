package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.api.WhistleBlowingInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;

public class WhistleBlowingHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        WhistleBlowingInfo mail = JsonUtil.fromJson(body, WhistleBlowingInfo.class);
        Logs.API.debug("收到举报内容:%s", mail);
        // TODO 暂时不处理
        HttpServer.sendOk(httpExchange, null);
        httpExchange.close();
    }
}
