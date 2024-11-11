package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.api.GetLogAccountRemain;
import com.xiuxiu.app.protocol.api.GetLogAccountRemainResp;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.statistics.LogAccountRemain;
import com.xiuxiu.app.server.statistics.StatManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;

public class GetLogAccountRemainHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Logs.API.debug("收到查询留存请求");
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        GetLogAccountRemain info = JsonUtil.fromJson(body, GetLogAccountRemain.class);
        if (0 == info.timeEnd) {
            info.timeEnd = System.currentTimeMillis()/1000;
        }
        GetLogAccountRemainResp resp = new GetLogAccountRemainResp();
        List<LogAccountRemain> logs = StatManager.I.getDailyLogAccountRemain(info.timeBegin, info.timeEnd);
        for (LogAccountRemain item : logs) {
            GetLogAccountRemainResp.LogAccountRemain log = new GetLogAccountRemainResp.LogAccountRemain();
            log.date = item.getDate();
            log.registerNum = item.getRegisterNum();
            log.day_2 = item.getDay_2();
            log.day_3 = item.getDay_3();
            log.day_4 = item.getDay_4();
            log.day_5 = item.getDay_5();
            log.day_6 = item.getDay_6();
            log.day_7 = item.getDay_7();
            log.day_14 = item.getDay_14();
            log.day_30 = item.getDay_30();
            resp.data.add(log);
        }
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
