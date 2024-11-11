package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.MoneyExpendRecordList;
import com.xiuxiu.app.protocol.api.MoneyExpendRecordListResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.statistics.constant.EMoneyExpendRoomType;
import com.xiuxiu.app.server.statistics.moneyrecord.MoneyExpendEveryDayRecord;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

/**
 * 查询房卡消耗记录
 */
public class MoneyExpendRecordListHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        MoneyExpendRecordList info = JsonUtil.fromJson(body, MoneyExpendRecordList.class);
        Logs.API.debug("查询房卡消耗记录:%s", info);
        MoneyExpendRecordListResp resp = new MoneyExpendRecordListResp();
        String sign = MD5Util.getMD5(info.playerUid, info.roomType, info.page, info.pageSize, Config.APP_KEY);
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                Logs.API.warn("数据被串改");
                resp.setRet(ErrorCode.REQUEST_INVALID_DATA);
                break;
            }
            if (info.playerUid <= 0) {
                Logs.API.warn("搜索的玩家不存在 %d", info.playerUid);
                resp.setRet(ErrorCode.PLAYER_NOT_EXISTS);
                break;
            }
            // info.roomType = 0,大厅、亲友圈、联盟  3,大厅  4,亲友圈  5,联盟
            List<MoneyExpendEveryDayRecord> list = null;
            if (info.roomType == EMoneyExpendRoomType.NORMAL.getValue()){
                list = DBManager.I.getMoneyExpendRecordDao().loadMoneyExpendEveryDayRecordByPlayerUid(info.playerUid, info.page * info.pageSize, info.pageSize);
            }else if (info.roomType == EMoneyExpendRoomType.LOBBY.getValue() || info.roomType == EMoneyExpendRoomType.GROUP.getValue() || info.roomType == EMoneyExpendRoomType.LEAGUE.getValue()){
                list = DBManager.I.getMoneyExpendRecordDao().loadMoneyExpendEveryDayRecord(info.playerUid, info.roomType, info.page * info.pageSize, info.pageSize);
            }
            if (null != list) {
                for (MoneyExpendEveryDayRecord record : list) {
                    MoneyExpendRecordListResp.MoneyExpendRecord moneyExpendRecord = new MoneyExpendRecordListResp.MoneyExpendRecord();
                    moneyExpendRecord.count = record.getCount();
                    moneyExpendRecord.expendTime = record.getTime();
                    moneyExpendRecord.roomType = record.getRoomType();
                    resp.info.add(moneyExpendRecord);
                }
            }
            resp.page = info.page;
            resp.pageSize = info.pageSize;
        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
