package com.xiuxiu.app.server.services.api.club;

import java.io.IOException;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.club.GetClubExpend;
import com.xiuxiu.app.protocol.api.temp.club.GetClubExpendResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.statistics.moneyrecord.MoneyExpendEveryDayRecord;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

public class GetClubExpendHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        GetClubExpend info = JsonUtil.fromJson(body, GetClubExpend.class);
        Logs.API.debug("查询俱乐部列表:%s", info);
        GetClubExpendResp resp = new GetClubExpendResp();
        String sign = MD5Util.getMD5(info.clubUid, info.page, info.pageSize, Config.APP_KEY);
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            IClub club = ClubManager.I.getClubByUid(info.clubUid);
            if (club == null) {
                resp.ret = ErrorCode.GROUP_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.GROUP_NOT_EXISTS.getMsg();
                break;
            }
            List<MoneyExpendEveryDayRecord> list = DBManager.I.getMoneyExpendRecordDao().loadMoneyExpendEveryDayRecordByClubUid(info.clubUid,info.page * info.pageSize, info.pageSize);
            for (MoneyExpendEveryDayRecord record : list) {
                GetClubExpendResp.clubExpend temp = new GetClubExpendResp.clubExpend();
                temp.expend = record.getCount();
                temp.time = record.getTime();

                resp.list.add(temp);
            }
            resp.clubUid = club.getClubUid();
            resp.clubName = club.getName();
            resp.page = info.page;
            resp.next = list.size() == Constant.PAGE_CNT_10;
        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
