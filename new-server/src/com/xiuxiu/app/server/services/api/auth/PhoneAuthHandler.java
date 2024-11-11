package com.xiuxiu.app.server.services.api.auth;

import java.io.IOException;
import java.util.HashMap;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.Switch;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;

public class PhoneAuthHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        HashMap<String, Object> param = JsonUtil.fromJson(body, HashMap.class);
        Logs.API.debug("收到手机验证开关:%s", body);
        Switch.PHONE_AUTH = (boolean) param.getOrDefault("use", false);
        HttpServer.sendOk(httpExchange, null);
        httpExchange.close();
    }
}
