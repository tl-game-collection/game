package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.GetAssistantWeChatListResp;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.system.AssistantWeChat;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;

public class GetAssistantWeChatListHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        Logs.API.debug("收到查询微信客服列表请求 body:%s", body);
        GetAssistantWeChatListResp resp = new GetAssistantWeChatListResp();
        resp.ret = ErrorCode.OK.getRet();
        resp.msg = ErrorCode.OK.getMsg();
        do {
            List<AssistantWeChat> lists = DBManager.I.getAssistantWeChatDAO().loadAll();
            for( AssistantWeChat weChat: lists) {
                GetAssistantWeChatListResp.WechatInfo wechatInfo = new GetAssistantWeChatListResp.WechatInfo();
                wechatInfo.location = weChat.getProvince() + weChat.getCity() + weChat.getDistrict();
                if ("".equals(wechatInfo.location)) {
                    wechatInfo.location = "全国";
                }
                wechatInfo.weChat = weChat.getWeChat();
                wechatInfo.adCode = weChat.getAdCode() + "";
                resp.data.add(wechatInfo);
            }
        } while (false);
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
