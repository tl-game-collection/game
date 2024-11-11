package com.xiuxiu.app.server.services.api.game;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.player.AddWhiteList;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;
import com.xiuxiu.core.utils.TimeUtil;

import java.io.IOException;

public class AddWhiteListHandler  implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        AddWhiteList info = JsonUtil.fromJson(body, AddWhiteList.class);
        String sign = MD5Util.getMD5(info.playerUid, info.PokerAndMajong, info.NiuNiu, info.JinHua, info.SanGong, Config.APP_KEY);
        ErrorMsg resp = new ErrorMsg();
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            Player player = PlayerManager.I.getPlayer(info.playerUid);
            if (null == player) {
                resp.ret = ErrorCode.PLAYER_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.PLAYER_NOT_EXISTS.getMsg();
                break;
            }
            resp.ret = ErrorCode.OK.getRet();
            resp.msg = ErrorCode.OK.getMsg();
            Config.addWHITE(player.getUid(), info.PokerAndMajong, info.NiuNiu, info.JinHua, info.SanGong);
            Logs.API.warn("添加%d进WHITE 时间:%s", info.playerUid, TimeUtil.format("yyyy-MM-dd HH:mm:ss.SSS", System.currentTimeMillis()));
        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
