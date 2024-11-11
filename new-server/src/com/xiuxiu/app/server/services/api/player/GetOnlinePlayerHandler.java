package com.xiuxiu.app.server.services.api.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.api.GetOnlinePlayerResp;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;

public class GetOnlinePlayerHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Logs.API.debug("收到查询在线人数请求");
        int count = PlayerManager.I.countOfOnlinePlayers();
        GetOnlinePlayerResp resp = new GetOnlinePlayerResp();
        ConcurrentHashMap<Long, Player> map=PlayerManager.I.getOnlinePlayer();
        List<Long> uids=new ArrayList<>();
        Iterator<Entry<Long, Player>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Long, Player> entry = it.next();
            System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());
            uids.add(entry.getKey());
        }
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(uids).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
