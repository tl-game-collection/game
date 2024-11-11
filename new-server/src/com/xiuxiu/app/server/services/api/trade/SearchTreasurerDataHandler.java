package com.xiuxiu.app.server.services.api.trade;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.trade.ChangeTreasurerDataInfo;
import com.xiuxiu.app.protocol.api.temp.trade.SearchTreasureDataInfo;
import com.xiuxiu.app.protocol.api.temp.trade.SearchTreasureDataInfoResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubInfo;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

import java.io.IOException;


public class SearchTreasurerDataHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Logs.API.warn("收到获取营商下分配置请求");

        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        SearchTreasureDataInfo info = JsonUtil.fromJson(body, SearchTreasureDataInfo.class);

        //check something
        String sign = MD5Util.getMD5(info.clubUid, Config.APP_KEY);

        SearchTreasureDataInfoResp resp = new SearchTreasureDataInfoResp();
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }

            IClub club = ClubManager.I.getClubByUid(info.clubUid);
            if (club == null) {
                resp.ret = ErrorCode.CLUB_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.CLUB_NOT_EXISTS.getMsg();
                break;
            }

            //如果合过圈
            if (club.checkIsJoinInMainClub()) {
                if (!club.checkIsMainClub()) {
                    resp.ret = ErrorCode.CLUB_NOT_MAIN_CLUB.getRet();
                    resp.msg = ErrorCode.CLUB_NOT_MAIN_CLUB.getMsg();
                    break;
                }
            }

            ClubInfo clubInfo = club.getClubInfo();
            resp.isFreeFirst = clubInfo.checkTreasurerFirstFree();
            resp.serviceChargePercentage = clubInfo.getTreasurerServiceChargePercentage();
            resp.canDownGoldMinValue = clubInfo.getTreasurerCanDownGoldMinValue();
            resp.desc = clubInfo.getTreasurerDesc();
            resp.clubUid = info.clubUid;
            //resp.ret =ErrorCode.OK.getRet();
            //resp.msg = ErrorCode.OK.getMsg();

        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
