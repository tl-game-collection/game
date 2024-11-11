package com.xiuxiu.app.server.services.api.player;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.player.SetPlayerName;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfChangeNameInfo;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;
import com.xiuxiu.core.utils.StringUtil;

import java.io.IOException;

/**
 * 修改玩家昵称
 * @date 2020/1/22 10:01
 * @author luocheng
 */
public class SetPlayerNameHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Logs.API.debug("收到修改玩家昵称请求");
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        SetPlayerName info = JsonUtil.fromJson(body, SetPlayerName.class);
        String sign = MD5Util.getMD5(info.playerUid, info.newName, Config.APP_KEY);
        ErrorMsg resp = new ErrorMsg();
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            Player player = PlayerManager.I.getPlayer(info.playerUid);
            if (player == null) {
                resp.ret = ErrorCode.PLAYER_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.PLAYER_NOT_EXISTS.getMsg();
                break;
            }
            if (StringUtil.isEmptyOrNull(info.newName) || info.newName.length() >= Constant.LEN_NAME) {
                resp.ret = ErrorCode.API_NAME_LENGTH.getRet();
                resp.msg = ErrorCode.API_NAME_LENGTH.getMsg();
                break;
            }
            player.changeName(info.newName);
            resp.ret = ErrorCode.OK.getRet();
            resp.msg = ErrorCode.OK.getMsg();

            player.send(CommandId.CLI_NTF_PLAYER_CHANGE_NAME_OK, new PCLIPlayerNtfChangeNameInfo(info.newName));
        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
