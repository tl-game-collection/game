package com.xiuxiu.app.server.services.api.diamond;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.api.temp.club.GetDailyDiamondCost;
import com.xiuxiu.app.protocol.api.temp.club.GetDailyDiamondCostResp;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.statistics.moneyrecord.MoneyExpendEveryDayRecord;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;

public class GetDiamondCostDailyListHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Logs.API.debug("收到查询每日钻石消耗请求");
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        GetDailyDiamondCost info = JsonUtil.fromJson(body, GetDailyDiamondCost.class);
        GetDailyDiamondCostResp resp = new GetDailyDiamondCostResp();
        if (0 == info.timeEnd) {
            info.timeEnd = System.currentTimeMillis();
        }
        List<MoneyExpendEveryDayRecord> list = new ArrayList<>();
        if (info.gameType != 0) {
            if (info.clubUid != 0) {
                list = DBManager.I.getMoneyExpendRecordDao().loadMoneyExpendEveryDayRecordByGameTypeAndClubUid(info.gameType,info.clubUid,info.timeBegin,info.timeEnd);
            } else {
                list = DBManager.I.getMoneyExpendRecordDao().loadMoneyExpendEveryDayRecordByGameType(info.gameType,info.timeBegin,info.timeEnd);
            }
        } else {
            if (info.clubUid != 0) {
                list = DBManager.I.getMoneyExpendRecordDao().loadMoneyExpendEveryDayRecordByClubUid2(info.clubUid,info.timeBegin,info.timeEnd);
            } else {
                list = DBManager.I.getMoneyExpendRecordDao().loadMoneyExpendEveryDayRecord2(info.timeBegin,info.timeEnd);
            }
        }
        for (MoneyExpendEveryDayRecord temp : list) {
            GetDailyDiamondCostResp.DailyDiamondCost dailyDiamondCost = new GetDailyDiamondCostResp.DailyDiamondCost();
            dailyDiamondCost.date = temp.getCreateTime();
            dailyDiamondCost.cost = temp.getCount();
            resp.data.add(dailyDiamondCost);
        }
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
