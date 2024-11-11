package com.xiuxiu.app.server.services.api.game;

import java.io.IOException;
import java.util.Iterator;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.api.GetWhiteListResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;

public class GetWhiteListHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
//        Logs.API.debug("获取白名单列表");
//
//        GetWhiteListResp resp = new GetWhiteListResp();
//        Iterator<Long> it = Config.getWHITE().iterator();
//        while (it.hasNext()) {
//            Player player = PlayerManager.I.getPlayer(it.next());
//            if (null == player) {
//                continue;
//            }
//            resp.getWhiteList().putIfAbsent(player.getUid(), player.getName());
//        }
//
//        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
//        httpExchange.close();
    }
}
