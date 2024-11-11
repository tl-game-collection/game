package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.PlayerWalletRecharge;
import com.xiuxiu.app.protocol.api.PlayerWalletRechargeResp;
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

public class PlayerWalletRechargeHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        PlayerWalletRecharge info = JsonUtil.fromJson(body, PlayerWalletRecharge.class);
        Logs.API.debug("钱包支付成功 充值:%s", info);
        String sign = MD5Util.getMD5(info.uid, info.total, Config.APP_KEY);

        PlayerWalletRechargeResp resp = new PlayerWalletRechargeResp();
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

            player.addMoney(EMoneyType.WALLET, info.total, player.getUid(),-1, EMoneyExpendType.NORMAL,-1);
        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
