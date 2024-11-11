package com.xiuxiu.app.server.services.api.player;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.player.AddUserDiamondByDaTang;
import com.xiuxiu.app.protocol.api.temp.player.AddUserDiamondByDaTangResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.constant.EMoneyType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.statistics.constant.EMoneyExpendType;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

import java.io.IOException;

/**
 * 后台操作 房卡消耗记录
 */
public class AddUserMoneyByDATangHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        AddUserDiamondByDaTang info = JsonUtil.fromJson(body, AddUserDiamondByDaTang.class);
        Logs.API.debug("添加用户货币:%s", info);
        String sign = MD5Util.getMD5(info.uid, info.amount, info.type, info.fromUid, info.operatorUid, Config.APP_KEY);
        AddUserDiamondByDaTangResp resp = new AddUserDiamondByDaTangResp();
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            Player player = PlayerManager.I.getPlayer(info.uid);
            if (null == player) {
                resp.ret = ErrorCode.PLAYER_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.PLAYER_NOT_EXISTS.getMsg();
                break;
            }
            if (info.type < EMoneyExpendType.NORMAL.ordinal() || info.type > EMoneyExpendType.LEAGUE_EXPEND_RETURN.ordinal()) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            if (player.addMoney(EMoneyType.DIAMOND, info.amount, player.getUid(), info.fromUid, EMoneyExpendType.values()[info.type], info.operatorUid)) {
                AddUserDiamondByDaTangResp.DiamondInfo diamondInfo = new AddUserDiamondByDaTangResp.DiamondInfo();
                diamondInfo.userUid = player.getUid();
                diamondInfo.currentDiamond = player.getMoneyByType(EMoneyType.DIAMOND);
                diamondInfo.sign = MD5Util.getMD5(diamondInfo.userUid, diamondInfo.currentDiamond, info.type, info.fromUid, info.operatorUid, Config.APP_KEY);
                resp.data = diamondInfo;
            } else {
                resp.ret = ErrorCode.PLAYER_LACK_DIAMOND.getRet();
                resp.msg = ErrorCode.PLAYER_LACK_DIAMOND.getMsg();
            }
        } while (false);
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
