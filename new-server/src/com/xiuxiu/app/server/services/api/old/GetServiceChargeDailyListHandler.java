package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.server.Logs;

public class GetServiceChargeDailyListHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Logs.API.debug("收到查询每日奖券请求");
//        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
//        GetDailyServiceCharge info = JsonUtil.fromJson(body, GetDailyServiceCharge.class);
//        if (0 == info.timeEnd) {
//            info.timeEnd = System.currentTimeMillis();
//        }
//        List<ServiceChargeDaily> lists;
//        if (0 != info.groupUid) {
//            lists = DBManager.I.getServiceChargeDailyDao().loadDailyByGroupUid(info.timeBegin, info.timeEnd, info.groupUid);
//        } else {
//            lists = DBManager.I.getServiceChargeDailyDao().loadByDaily(info.timeBegin, info.timeEnd);
//        }
//        GetDailyServiceChargeResp resp = new GetDailyServiceChargeResp();
//        for (ServiceChargeDaily item :lists) {
//            GetDailyServiceChargeResp.DailyServiceCharge list = new GetDailyServiceChargeResp.DailyServiceCharge();
//            list.date = item.getTime();
//            list.cost = item.getCost();
//            resp.data.add(list);
//        }
//        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
