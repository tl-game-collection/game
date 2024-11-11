package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.ChatTestInfo;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.chat.ChatManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;

public class ChatTestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        ChatTestInfo chatTestInfo = JsonUtil.fromJson(body, ChatTestInfo.class);
        String ip = httpExchange.getRequestHeaders().get("X-real-ip").get(0);
        Logs.API.debug("聊天测试:%s ip:%s", chatTestInfo, ip);
        ErrorMsg errorMsg = new ErrorMsg(ErrorCode.OK);
        if (!Config.IP_WHITE.contains(ip)) {
            errorMsg.ret = ErrorCode.REQUEST_INVALID.getRet();
            errorMsg.msg = ErrorCode.REQUEST_INVALID.getMsg();
        } else {
            Player fromPlayer = PlayerManager.I.getPlayer(chatTestInfo.getFromPlayerUid());
            Player toPlayer = PlayerManager.I.getPlayer(chatTestInfo.getToPlayerUid());
            if (null == fromPlayer || null == toPlayer) {
                errorMsg.ret = ErrorCode.PLAYER_NOT_EXISTS.getRet();
                errorMsg.msg = ErrorCode.PLAYER_NOT_EXISTS.getMsg();
            } else {
                for (int i = 0; i < chatTestInfo.getCnt(); ++i) {
                    ChatManager.I.chat(fromPlayer, (byte) 1, toPlayer.getUid(), (byte) 0, chatTestInfo.getSay(),-1,-1,-1);
                }
            }
        }

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(errorMsg).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
