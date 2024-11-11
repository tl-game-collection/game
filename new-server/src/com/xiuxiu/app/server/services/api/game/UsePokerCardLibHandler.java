package com.xiuxiu.app.server.services.api.game;

import java.io.IOException;
import java.util.HashMap;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;

public class UsePokerCardLibHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        HashMap<String, Object> param = JsonUtil.fromJson(body, HashMap.class);
        Logs.API.debug("收到使用牌组:%s", body);
        Switch.USE_CARD_LIB_POKER = (boolean) param.getOrDefault("use", false);
        HttpServer.sendOk(httpExchange, null);
        httpExchange.close();
    }
}
