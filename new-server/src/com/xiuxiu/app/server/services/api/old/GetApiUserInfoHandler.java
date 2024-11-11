package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.GetApiUserInfoResp;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.api.ApiManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;

public class GetApiUserInfoHandler extends BaseHttpHandler {
    @Override
    protected void doHandler(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        com.xiuxiu.app.protocol.api.GetApiUserInfo info = JsonUtil.fromJson(body, com.xiuxiu.app.protocol.api.GetApiUserInfo.class);
        Logs.API.debug("获取api用户信息:%s", info);

        GetApiUserInfoResp resp = new GetApiUserInfoResp();

        do {
            if (!ApiManager.I.isVerifyToken(info.token)) {
                resp.setRet(ErrorCode.REQUEST_INVALID_TOKEN);
                break;
            }
            resp.data = new GetApiUserInfoResp.ApiUserInfo();
            resp.data.name = ApiManager.I.getName();
            resp.data.avatar = "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif";
            resp.data.introduction = "";
            resp.data.roles = new String[] {"admin"};
        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
