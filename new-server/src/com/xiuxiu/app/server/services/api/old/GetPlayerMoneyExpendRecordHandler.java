package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.GetPlayerMoneyExpendRecord;
import com.xiuxiu.app.protocol.api.GetPlayerMoneyExpendRecordResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.statistics.consume.PlayerMoneyConsumeRecord;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

/**
 * 查询根据用户playerId获取所有所有类型的房卡消耗数量
 */
public class GetPlayerMoneyExpendRecordHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        GetPlayerMoneyExpendRecord info = JsonUtil.fromJson(body, GetPlayerMoneyExpendRecord.class);
        Logs.API.debug("查询根据用户playerId获取所有所有类型的房卡消耗数量:%s", info);
        GetPlayerMoneyExpendRecordResp resp = new GetPlayerMoneyExpendRecordResp();
        String sign = MD5Util.getMD5(info.playerUid, Config.APP_KEY);
        if (!sign.equalsIgnoreCase(info.sign)) {
            Logs.API.warn("数据被串改");
            resp.setRet(ErrorCode.REQUEST_INVALID_DATA);
        } else {
            PlayerMoneyConsumeRecord data = DBManager.I.getPlayerMoneyConsumeRecordDAO().load(info.playerUid);
            if (data != null) {
                resp.count = data.getValue1() + data.getValue2() + data.getValue3();
            }
        }
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
