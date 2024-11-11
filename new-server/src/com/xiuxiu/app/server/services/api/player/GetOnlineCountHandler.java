package com.xiuxiu.app.server.services.api.player;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.api.GetOnlineCountResp;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;

public class GetOnlineCountHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Logs.API.debug("收到查询在线人数请求");
        int count = PlayerManager.I.countOfOnlinePlayers();
        GetOnlineCountResp resp = new GetOnlineCountResp();
        resp.data.onlineNum = count;
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
