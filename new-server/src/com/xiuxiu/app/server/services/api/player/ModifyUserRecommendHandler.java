package com.xiuxiu.app.server.services.api.player;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.player.ModifyUserRecommend;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

public class ModifyUserRecommendHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        ModifyUserRecommend info = JsonUtil.fromJson(body, ModifyUserRecommend.class);
        Logs.API.debug("管理员修改玩家:%s", info);
        String sign = MD5Util.getMD5(info.uid, info.targetUid, Config.APP_KEY);
        ErrorMsg resp = new ErrorMsg();
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
            if (0 == info.targetUid) {
                info.targetUid = -1L;
            }
            if (-1L != info.targetUid) {
                Player targetPlayer = PlayerManager.I.getPlayer(info.targetUid);
                if (null == targetPlayer) {
                    resp.ret = ErrorCode.PLAYER_NOT_EXISTS.getRet();
                    resp.msg = ErrorCode.PLAYER_NOT_EXISTS.getMsg();
                    break;
                }
            }
            player.changeRecommendPlayerUid(info.targetUid);
        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
