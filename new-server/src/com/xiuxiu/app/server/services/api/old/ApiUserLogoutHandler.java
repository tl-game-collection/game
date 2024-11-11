package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.api.ApiManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;

public class ApiUserLogoutHandler extends BaseHttpHandler {
    @Override
    protected void doHandler(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        Logs.API.debug("api用户登出");

        ApiManager.I.logout();

        HttpServer.sendOk(httpExchange, null);
        httpExchange.close();
    }
}
