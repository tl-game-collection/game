package com.xiuxiu.app.server.services.api.game;

import java.io.IOException;
import java.util.HashMap;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.app.server.room.CardLibraryManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;

public class SetMahjongCardLibHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        HashMap<String, Object> param = JsonUtil.fromJson(body, HashMap.class);
        Logs.API.debug("收到设置牌组:%s", body);
        String card = (String) param.getOrDefault("card", "");
        Switch.USE_CARD_LIB = (boolean) param.getOrDefault("use", false);
        CardLibraryManager.I.setMahjongCardLib(card);
        HttpServer.sendOk(httpExchange, null);
        httpExchange.close();
    }
}
