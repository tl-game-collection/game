package com.xiuxiu.app.server.services.api.trade;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.trade.ChangeTreasurerDataInfo;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubInfo;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;
import com.xiuxiu.core.utils.StringUtil;

import java.io.IOException;

public class ChangeTreasureInfoHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Logs.API.warn("收到设置营商下分配置请求");

        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        ChangeTreasurerDataInfo info = JsonUtil.fromJson(body, ChangeTreasurerDataInfo.class);

        //check something
        String sign = MD5Util.getMD5(info.clubUid, info.serviceChargePercentage, Config.APP_KEY);

        ErrorMsg resp = new ErrorMsg();
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

            if (info.canDownGoldMinValue < 0
                    || info.serviceChargePercentage < 0
                    || info.serviceChargePercentage > 100){
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            }

            if (StringUtil.isEmptyOrNull(info.desc)){
                info.desc = "";
            }

            if (info.desc.length() > 3600){
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            }

            ClubInfo clubInfo = club.getClubInfo();
            clubInfo.setTreasurerFirstFree(info.isFreeFirst);
            clubInfo.setTreasurerServiceChargePercentage(info.serviceChargePercentage);
            clubInfo.setTreasurerCanDownGoldMinValue(info.canDownGoldMinValue);
            clubInfo.setTreasurerDesc(info.desc);
            clubInfo.setDirty(true);
            //resp.ret =ErrorCode.OK.getRet();
            //resp.msg = ErrorCode.OK.getMsg();

        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
