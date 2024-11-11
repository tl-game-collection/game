package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.api.GetDailyActiveCount;
import com.xiuxiu.app.protocol.api.GetDailyActiveCountResp;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.statistics.StatManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;

public class GetDailyActiveCountHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Logs.API.debug("收到查询日活人数请求");
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        GetDailyActiveCount info = JsonUtil.fromJson(body, GetDailyActiveCount.class);
        if (0 == info.timeEnd) {
            info.timeEnd = System.currentTimeMillis()/1000;
        }
        GetDailyActiveCountResp resp = new GetDailyActiveCountResp();
        List<Map<String, Object>> dailyActive = StatManager.I.getDailyActive(info.timeBegin, info.timeEnd);
        for (Map<String, Object> item : dailyActive) {
            GetDailyActiveCountResp.ActiveCount activeCount = new GetDailyActiveCountResp.ActiveCount();
            activeCount.date = item.get("days").toString();
            activeCount.activeNum = Integer.parseInt(item.get("times").toString());
            resp.data.add(activeCount);
        }
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
