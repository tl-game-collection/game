package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.ModifyAssistantWeChat;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.system.AssistantWeChat;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.StringUtil;

public class ModifyAssistantWeChatHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        ModifyAssistantWeChat info = JsonUtil.fromJson(body, ModifyAssistantWeChat.class);
        Logs.API.debug("收到编辑微信客服请求 %s", info);
        ErrorMsg resp = new ErrorMsg();
        resp.ret = ErrorCode.OK.getRet();
        resp.msg = ErrorCode.OK.getMsg();
        do {
            if (StringUtil.isEmptyOrNull(info.weChat)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            AssistantWeChat weChat = DBManager.I.getAssistantWeChatDAO().loadByAdCode(info.adCode);
            if (null == weChat) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            weChat.setWeChat(info.weChat);
            if (!DBManager.I.getAssistantWeChatDAO().save(weChat)) {
                Logs.API.debug("保存微信客服失败 %s", info);
                resp.ret = ErrorCode.SERVER_DB_ERROR.getRet();
                resp.msg = ErrorCode.SERVER_DB_ERROR.getMsg();
                break;
            }
        } while (false);
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
