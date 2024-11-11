package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;
import java.util.HashMap;

import com.sun.net.httpserver.HttpExchange;
import com.xiuxiu.app.server.api.ApiManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;

public class GetStatusInfoHandler extends BaseHttpHandler {
    @Override
    protected void doHandler(HttpExchange httpExchange) throws IOException {
        HashMap<String, Object> resp = new HashMap<>(3);
        resp.put("online", ApiManager.I.isOnline());
        resp.put("token", ApiManager.I.getToken());
        resp.put("lastOpTime", ApiManager.I.getLastOpTime());
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
