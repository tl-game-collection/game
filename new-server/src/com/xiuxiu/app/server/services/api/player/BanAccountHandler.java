package com.xiuxiu.app.server.services.api.player;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.player.BanAccount;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfKill;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.account.AccountManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

public class BanAccountHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        BanAccount info = JsonUtil.fromJson(body, BanAccount.class);
        Logs.API.debug("添加封号:%s", info);
        String sign = MD5Util.getMD5(info.getPlayerUid(), info.isBan(), Config.APP_KEY);
        ErrorMsg resp = new ErrorMsg();
        do {
            if (!sign.equalsIgnoreCase(info.getSign())) {
                Logs.API.warn("数据被串改");
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            if (AccountManager.I.ban(info.getPlayerUid(), info.isBan())) {
                Logs.API.debug("封号/解封成功");
                if (info.isBan()) {
                    Player player = PlayerManager.I.getOnlinePlayer(info.getPlayerUid());
                    if (null != player) {
                        player.changeRoomId(-1, -1);
                        PCLIPlayerNtfKill kill = new PCLIPlayerNtfKill();
                        kill.reason = "被封禁";
                        player.send(CommandId.CLI_NTF_PLAYER_KILL, kill);
                        player.logout(true);
                    }
                }
                resp.ret = ErrorCode.OK.getRet();
                resp.msg = ErrorCode.OK.getMsg();
            } else {
                Logs.API.debug("封号/解封失败");
                resp.ret = ErrorCode.ACCOUNT_BAN_FAIL.getRet();
                resp.msg = ErrorCode.ACCOUNT_BAN_FAIL.getMsg();
            }
        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
